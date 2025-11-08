package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xingzhi.shortvideosharingplatform.entity.*;
import com.xingzhi.shortvideosharingplatform.mapper.VideoRecommendMapper;
import com.xingzhi.shortvideosharingplatform.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.utils.DateUtils;
import com.xingzhi.shortvideosharingplatform.vo.HotVideoVO;
import com.xingzhi.shortvideosharingplatform.vo.RecommendVideoVO;
import com.xingzhi.shortvideosharingplatform.vo.SimilarVideoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * 视频推荐表 服务实现类
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@Service
public class VideoRecommendServiceImpl extends ServiceImpl<VideoRecommendMapper, VideoRecommend> implements IVideoRecommendService {


    @Resource
    private IStatDailyService statDailyService;
    @Resource
    private IUserBehaviorLogsService userBehaviorLogsService;

    @Resource
    private IVideoService videoService;
    //基于视频内容 向量映射：videoId -> 特征向量
    private final Map<Long, double[]> videoFeatureMap = new ConcurrentHashMap<>();

    // 标签词典：标签名 -> 索引
    private final Map<String, Integer> tagIndexMap = new ConcurrentHashMap<>();

    // 类别词典：类别ID -> 索引
    private final Map<Integer, Integer> categoryIndexMap = new ConcurrentHashMap<>();

    // 特征向量总维度
    private int featureDimension = 0;

    // 基于用户行为类型权重配置
    private static final Map<Integer, Double> BEHAVIOR_WEIGHTS = new HashMap<>();
    static {
        BEHAVIOR_WEIGHTS.put(2, 0.3);  // 点赞
        BEHAVIOR_WEIGHTS.put(3, 0.25); // 收藏
        BEHAVIOR_WEIGHTS.put(4, 0.25); // 分享
        BEHAVIOR_WEIGHTS.put(1, 0.1);  // 播放
        BEHAVIOR_WEIGHTS.put(6, 0.3);  // 评论
        BEHAVIOR_WEIGHTS.put(7, 0.2);  // 完播
        BEHAVIOR_WEIGHTS.put(5, 0.05); // 推荐
    }

    // 时间衰减因子
    private static final double TIME_DECAY_FACTOR = 0.95;
    // 用户兴趣向量缓存（userId -> 兴趣向量）
    private final Map<Long, Map<Long, Double>> userInterestVectors = new ConcurrentHashMap<>();

    // 视频相似度矩阵（videoId -> 相似视频列表）
    private final Map<Long, List<SimilarVideoVO>> videoSimilarityMatrix = new ConcurrentHashMap<>();

   public void init(){
        List<Video> videos = videoService.list();
        buildFeatureIndex(videos);
        extractVideoFeatures(videos);
    }
    /**
     * 获取指定日期热度最高的10个视频
     *   基于热度推荐给用户的视频 后续可用redis缓存
     * @return 热度Top6的视频列表
     */
    @Override
    public List<HotVideoVO> getTopHotVideos() {
        // 查询所有视频统计数据
        List<StatDaily> statList = statDailyService.list();

        // 设置时间衰减参数（可根据业务调整）
        double decayFactor = 0.9; // 每日衰减系数
        LocalDate currentDate = LocalDate.now();

        // 计算热度并排序
        List<HotVideoVO> hotVideoList = statList.stream()
                .map(stat -> {
                    HotVideoVO vo = new HotVideoVO();
                    vo.setVideoId(stat.getVideoId());

                    // 获取统计日期与当前日期的间隔天数
                    long daysAgo = DateUtils.daysBetween(stat.getStatDate(), currentDate);
                    // 计算基础热度分数（权重可根据业务调整）
                    double behaviorScore = stat.getViewCount() * 0.2
                            + stat.getLikeCount() * 0.2
                            + stat.getCommentCount() * 0.2
                            + stat.getShareCount() * 0.1
                            + stat.getRetentionRate() * 0.2
                            + stat.getDanmuCount() * 0.1;
                    // 应用时间衰减：e^(-λ*t)，其中λ为衰减因子，t为天数
                    double timeDecay = Math.pow(decayFactor, daysAgo);
                    // 最终热度分数 = 基础分数 × 时间衰减因子
                    double heat = behaviorScore * timeDecay;
                    vo.setHotScore(heat);
                    return vo;
                })
                .sorted(Comparator.comparingDouble(HotVideoVO::getHotScore).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return hotVideoList;
    }
    /**
     * 基于内容推荐视频
     *
     */
    @Override
    public List<Video> recommendByContent(Long userId) {
        init();
        // 主查询：通过子查询获取视频列表 排除自己的视频
        LambdaQueryWrapper<Video> queryWrapper = Wrappers.lambdaQuery();
     queryWrapper.inSql(Video::getId,  "SELECT video_id FROM user_behavior_logs WHERE user_id = " + userId)
             .ne(Video::getUserId, userId)
                .orderByDesc(Video::getCreatedTime);  // 按视频创建时间排序
        List<Video> userHistoryVideos= videoService.list(queryWrapper);
        if (userHistoryVideos.isEmpty()) {
            List<HotVideoVO> topHotVideos = getTopHotVideos();
            // 用户冷启动：推荐热门视频
            return topHotVideos.stream()
                    .map(vo -> videoService.getById(vo.getVideoId()))
                    .collect(Collectors.toList());
        }

        // 构建用户兴趣向量（基于历史视频特征的加权平均）
        double[] userInterestVector = buildUserInterestVector(userHistoryVideos);

        // 计算所有视频与用户兴趣向量的相似度  算出视频推荐的得分
        List<RecommendVideoVO> recommendVideos = calculateSimilarity(userInterestVector);

        // 获取用户自己发布的视频ID集合 排除发布的视频
        Set<Long> userPublishedVideoIds = videoService.list(
                Wrappers.<Video>lambdaQuery().eq(Video::getUserId, userId)
        ).stream().map(Video::getId).collect(Collectors.toSet());

        // 过滤掉用户已看过的视频和用户自己发布的视频
        Set<Long> viewedVideoIds = userHistoryVideos.stream()
                .map(Video::getId)
                .collect(Collectors.toSet());


        List<RecommendVideoVO> filteredRecommendations = recommendVideos.stream()
                .filter(video -> !viewedVideoIds.contains(video.getVideoId()))
                .filter(video -> !userPublishedVideoIds.contains(video.getVideoId()))
                .limit(1)
                .collect(Collectors.toList());

        // 获取推荐视频详情
        List<Long> recommendVideoIds = filteredRecommendations.stream()
                .map(RecommendVideoVO::getVideoId)
                .collect(Collectors.toList());
        List<Video> videoList = videoService.listByIds(recommendVideoIds);

        return videoList;
    }


    // 构建标签和类别的索引映射
    private void buildFeatureIndex(List<Video> videos) {
        // 提取所有标签
        Set<String> allTags = new HashSet<>();
        for (Video video : videos) {
            if (video.getTags() != null && !video.getTags().isEmpty()) {
                String[] tags = video.getTags().split(",");
                allTags.addAll(Arrays.asList(tags));
            }
        }

        // 构建标签索引
        int index = 0;
        for (String tag : allTags) {
            tagIndexMap.put(tag, index++);
        }

        // 提取所有类别
        Set<Integer> allCategories = videos.stream()
                .map(Video::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 构建类别索引
        for (Integer categoryId : allCategories) {
            categoryIndexMap.put(categoryId, index++);
        }

        featureDimension = index;
    }
    // 提取视频特征向量
    private void extractVideoFeatures(List<Video> videos) {
        for (Video video : videos) {
            double[] featureVector = new double[featureDimension];

            // 处理标签特征
            if (video.getTags() != null && !video.getTags().isEmpty()) {
                String[] tags = video.getTags().split(",");
                for (String tag : tags) {
                    if (tagIndexMap.containsKey(tag)) {
                        featureVector[tagIndexMap.get(tag)] = 1.0;
                    }
                }
            }

            // 处理类别特征
            if (video.getCategoryId() != null && categoryIndexMap.containsKey(video.getCategoryId())) {
                featureVector[categoryIndexMap.get(video.getCategoryId())] = 1.0;
            }

            // 存储特征向量
            videoFeatureMap.put(video.getId(), featureVector);
        }
    }
    // 构建用户兴趣向量
    private double[] buildUserInterestVector(List<Video> userHistoryVideos) {
        double[] interestVector = new double[featureDimension];

        // 简单平均：每个历史视频权重相同
        for (Video video : userHistoryVideos) {
            double[] videoFeature = videoFeatureMap.getOrDefault(video.getId(), new double[featureDimension]);
            for (int i = 0; i < featureDimension; i++) {
                interestVector[i] += videoFeature[i];
            }
        }

        // 归一化处理
        normalizeVector(interestVector);

        return interestVector;
    }
    // 向量归一化
    private void normalizeVector(double[] vector) {
        double norm = 0.0;
        for (double value : vector) {
            norm += value * value;
        }
        norm = Math.sqrt(norm);

        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
    // 计算相似度并排序
    private List<RecommendVideoVO> calculateSimilarity(double[] userInterestVector) {
        List<RecommendVideoVO> recommendVideos = new ArrayList<>();

        for (Map.Entry<Long, double[]> entry : videoFeatureMap.entrySet()) {
            Long videoId = entry.getKey();
            double[] videoFeature = entry.getValue();

            // 计算余弦相似度
            double similarity = cosineSimilarity(userInterestVector, videoFeature);

            recommendVideos.add(new RecommendVideoVO(videoId, similarity));
        }

        // 按相似度降序排序
        recommendVideos.sort(Comparator.comparingDouble(RecommendVideoVO::getSimilarity).reversed());

        return recommendVideos;
    }

    // 计算余弦相似度
    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     *  基于用户行为推荐视频
     */
    @Override
    public List<Video> recommendByBehavior(Long userId, boolean refresh) {

        initBehavior();
        // 获取用户自己发布的视频ID集合（添加缓存以提高性能）
        Set<Long> userPublishedVideoIds = getUserPublishedVideoIds(userId);

        // 获取用户兴趣向量，并排除用户自己发布的视频
        Map<Long, Double> userInterest = userInterestVectors.getOrDefault(userId, new HashMap<>())
                .entrySet().stream()
                .filter(entry -> !userPublishedVideoIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (userInterest.isEmpty()) {
            List<HotVideoVO> topHotVideos = getTopHotVideos();
            // 用户冷启动：推荐热门视频
            return topHotVideos.stream()
                    .map(vo -> videoService.getById(vo.getVideoId()))
                    .collect(Collectors.toList());
        }

        // 基于用户兴趣和视频相似度生成推荐
        Map<Long, Double> recommendationScores = new HashMap<>();

        // 遍历用户感兴趣的视频
        for (Map.Entry<Long, Double> entry : userInterest.entrySet()) {
            Long videoId = entry.getKey();
            double interestScore = entry.getValue();

            // 获取相似视频列表（使用候选池索引选择不同的相似视频子集）
            List<SimilarVideoVO> similarVideos = videoSimilarityMatrix.getOrDefault(videoId, Collections.emptyList());


            // 如果是"换一换"请求，随机选择相似视频子集
            if (refresh) {
                // 随机生成候选池索引（0-10）
                int candidatePoolIndex = new Random().nextInt(10);

                // 基于索引选择不同的相似视频子集
                similarVideos = getSimilarVideosByPool(similarVideos, candidatePoolIndex);
            }
            // 计算推荐分数
            for (SimilarVideoVO similarVideo : similarVideos) {
                Long recommendedVideoId = similarVideo.getVideoId();
                double similarity = similarVideo.getSimilarity();

                // 累加推荐分数：兴趣分数 * 相似度
                recommendationScores.put(
                        recommendedVideoId,
                        recommendationScores.getOrDefault(recommendedVideoId, 0.0) +
                                (interestScore * similarity)
                );
            }
        }

        // 按推荐分数排序并获取前N个视频ID
        List<Long> recommendedVideoIds = recommendationScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .limit(1)
                .collect(Collectors.toList());

        // 查询视频详情
        return videoService.listByIds(recommendedVideoIds);
    }
    // 根据候选池索引获取不同的相似视频子集 基数够大就很少刷到之前的视频
    private List<SimilarVideoVO> getSimilarVideosByPool(List<SimilarVideoVO> allVideos, int poolIndex) {
        int step = 10;
        int startIndex = poolIndex % step;
        return IntStream.range(0, allVideos.size())
                .filter(i -> i % step == startIndex)
                .mapToObj(allVideos::get)
                .collect(Collectors.toList());
    }

    private Set<Long> getUserPublishedVideoIds(Long userId) {
        return videoService.list(new QueryWrapper<Video>().eq("user_id", userId))
                .stream()
                .map(Video::getId)
                .collect(Collectors.toSet());
    }

    public void initBehavior() {
        // 加载所有用户行为数据
        List<UserBehaviorLogs> allBehaviors = userBehaviorLogsService.list();

        // 构建用户兴趣向量
        buildUserInterestVectors(allBehaviors);

        // 构建视频相似度矩阵
        buildVideoSimilarityMatrix(allBehaviors);
    }
    /**
     * 构建用户兴趣向量
     */
    private void buildUserInterestVectors(List<UserBehaviorLogs> allBehaviors) {
        // 按用户分组行为日志
        Map<Long, List<UserBehaviorLogs>> userBehaviors = allBehaviors.stream()
                .collect(Collectors.groupingBy(UserBehaviorLogs::getUserId));

        // 为每个用户构建兴趣向量
        for (Map.Entry<Long, List<UserBehaviorLogs>> entry : userBehaviors.entrySet()) {
            Long userId = entry.getKey();
            List<UserBehaviorLogs> behaviors = entry.getValue();

            // 计算用户对每个视频的兴趣分数
            Map<Long, Double> interestVector = new HashMap<>();

            for (UserBehaviorLogs behavior : behaviors) {
                Long videoId = behavior.getVideoId();
                Integer behaviorType = behavior.getBehaviorType();
                LocalDate createdTime = LocalDate.from(behavior.getCreatedTime());

                // 获取行为权重
                double behaviorWeight = BEHAVIOR_WEIGHTS.getOrDefault(behaviorType, 0.0);

                // 计算时间衰减（行为越近，权重越高）
                long daysAgo = DateUtils.daysBetween(createdTime, LocalDate.from(LocalDateTime.now()));
                double timeDecay = Math.pow(TIME_DECAY_FACTOR, daysAgo);

                // 兴趣分数 = 行为权重 * 时间衰减
                double interestScore = behaviorWeight * timeDecay;

                // 累加同一视频的不同行为分数
                interestVector.put(
                        videoId,
                        interestVector.getOrDefault(videoId, 0.0) + interestScore
                );
            }

            // 存储用户兴趣向量
            userInterestVectors.put(userId, interestVector);
        }
    }
    /**
     * 构建视频相似度矩阵
     */
    private void buildVideoSimilarityMatrix(List<UserBehaviorLogs> allBehaviors) {
        // 按视频分组行为日志
        Map<Long, List<UserBehaviorLogs>> videoBehaviors = allBehaviors.stream()
                .collect(Collectors.groupingBy(UserBehaviorLogs::getVideoId));

        // 计算视频间的相似度
        for (Map.Entry<Long, List<UserBehaviorLogs>> entryA : videoBehaviors.entrySet()) {
            Long videoIdA = entryA.getKey();
            List<UserBehaviorLogs> behaviorsA = entryA.getValue();

            // 获取观看过视频A的用户集合
            Set<Long> usersA = behaviorsA.stream()
                    .map(UserBehaviorLogs::getUserId)
                    .collect(Collectors.toSet());

            // 存储视频A与其他视频的相似度
            Map<Long, Integer> coOccurrenceMap = new HashMap<>();

            // 遍历其他视频
            for (Map.Entry<Long, List<UserBehaviorLogs>> entryB : videoBehaviors.entrySet()) {
                Long videoIdB = entryB.getKey();

                // 跳过自身
                if (videoIdA.equals(videoIdB)) {
                    continue;
                }

                List<UserBehaviorLogs> behaviorsB = entryB.getValue();

                // 获取观看过视频B的用户集合
                Set<Long> usersB = behaviorsB.stream()
                        .map(UserBehaviorLogs::getUserId)
                        .collect(Collectors.toSet());

                // 计算共同观看用户数
                long coOccurrenceCount = usersA.stream()
                        .filter(usersB::contains)
                        .count();

                if (coOccurrenceCount > 0) {
                    coOccurrenceMap.put(videoIdB, (int) coOccurrenceCount);
                }
            }

            // 转换为相似度列表（归一化处理）
            List<SimilarVideoVO> similarVideos = new ArrayList<>();
            if (!coOccurrenceMap.isEmpty()) {
                int maxCoOccurrence = Collections.max(coOccurrenceMap.values());

                for (Map.Entry<Long, Integer> entry : coOccurrenceMap.entrySet()) {
                    Long similarVideoId = entry.getKey();
                    int coOccurrence = entry.getValue();

                    // 相似度 = 共同观看用户数 / 最大共同观看用户数
                    double similarity = (double) coOccurrence / maxCoOccurrence;

                    similarVideos.add(new SimilarVideoVO(similarVideoId, similarity));
                }

                // 按相似度排序
                similarVideos.sort(Comparator.comparingDouble(SimilarVideoVO::getSimilarity).reversed());

                // 存储视频A的相似视频列表（取前100个）
                videoSimilarityMatrix.put(videoIdA, similarVideos.stream().limit(100).collect(Collectors.toList()));
            }
        }
    }
}
