package com.xingzhi.shortvideosharingplatform.mapper;

import com.xingzhi.shortvideosharingplatform.dto.DailyStatisticsDTO;
import com.xingzhi.shortvideosharingplatform.dto.PlatformDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.UserVideoDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.VideoCountDTO;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingzhi.shortvideosharingplatform.vo.VideoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频信息表 Mapper 接口
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    /**
     * 查询所有视频列表（带分页）
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 视频列表
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.status = 2 " +  // 只查询已发布的视频
            "ORDER BY v.created_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<VideoVO> selectAllVideos(@Param("offset") Integer offset,
                                  @Param("pageSize") Integer pageSize);


    /**
     * 查询所有视频列表（带分页）
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 视频列表
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.status = 1 " +  // 只查询已发布的视频
            "ORDER BY v.created_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<VideoVO> selectAllShenHeVideoList(@Param("offset") Integer offset,
                                  @Param("pageSize") Integer pageSize);

    /**
     * 获取已审核视频的总数量（用于分页查询）
     * @return 视频总数量
     */
    @Select("SELECT COUNT(*) FROM video WHERE status = 1")
    Integer selectTotalShenHeVideoCount();
    /**
     * 查询所有视频总数（用于分页）
     * @return 视频总数
     */
    @Select("SELECT COUNT(*) FROM video WHERE status = 2")
    Integer selectAllVideoCount();

    /**
     * 根据视频ID查询视频详情
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.id = #{videoId}")
    VideoVO selectVideoDetailById(@Param("videoId") Long videoId);

    /**
     * 查询用户发布的视频列表
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.user_id = #{userId} " +
            "ORDER BY v.created_time DESC")
    List<VideoVO> selectVideosByUserId(@Param("userId") Long userId);

    /**
     * 查询分类下的视频列表
     */
    @Select("SELECT v.*, u.user_name as author " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.category_id = #{categoryId} AND v.status = 2 " +
            "ORDER BY v.created_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<VideoVO> selectVideosByCategoryId(@Param("categoryId") Integer categoryId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
     * 分页查询推荐视频列表
     */
    @Select("SELECT v.*, u.nickname as author " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.status = 2 " +
            "ORDER BY v.view_count DESC, v.created_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<VideoVO> selectRecommendVideos(@Param("offset") Integer offset,
                                        @Param("pageSize") Integer pageSize);

    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.status = 2 " +
            "AND (v.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR v.description LIKE CONCAT('%', #{keyword}, '%') " +
            "OR v.tags LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.user_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.nickname LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY v.created_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<VideoVO> searchVideos(@Param("keyword") String keyword,
                               @Param("offset") Integer offset,
                               @Param("pageSize") Integer pageSize);

    /**
     * 搜索视频总数（用于分页）
     * @param keyword 搜索关键词
     * @return 匹配的视频总数
     */
    @Select("SELECT COUNT(*) " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE v.status = 1 AND (" +
            "   MATCH(v.title, v.description, v.tags) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE) OR " +
            "   u.user_name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "   u.nickname LIKE CONCAT('%', #{keyword}, '%')" +
            ")")
    Integer countSearchVideos(@Param("keyword") String keyword);

    /**
     * 查询用户对视频的互动状态（是否点赞、是否收藏、是否关注作者）
     * @param videoId 视频ID
     * @param userId 用户ID
     * @return Map包含三个字段：
     *   isLiked(是否点赞),
     *   isFavorited(是否收藏),
     *   isFollowingAuthor(是否关注视频作者)
     */
    @Select("SELECT " +
            "  (SELECT COUNT(*) FROM video_like WHERE video_id = #{videoId} AND user_id = #{userId}) > 0 AS isLiked, " +
            "  (SELECT COUNT(*) FROM video_favorite WHERE video_id = #{videoId} AND user_id = #{userId}) > 0 AS isFavorited, " +
            "  (SELECT COUNT(*) FROM user_follow WHERE follower_id = #{userId} AND following_id = " +
            "    (SELECT user_id FROM video WHERE id = #{videoId})) > 0 AS isFollowingAuthor")
    Map<String, Boolean> selectUserInteractionStatus(@Param("videoId") Long videoId,
                                                     @Param("userId") Long userId);

    /**
     * 查询用户点赞过的所有视频列表
     * @param userId 用户ID
     * @return 点赞视频列表
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "JOIN video_like vl ON v.id = vl.video_id " +
            "WHERE vl.user_id = #{userId} AND v.status = 2 " +  // 只查询已发布的视频
            "ORDER BY vl.created_time DESC")  // 按点赞时间倒序排列
    List<VideoVO> selectAllLikedVideosByUserId(@Param("userId") Long userId);

    /**
     * 查询用户收藏的所有视频列表
     * @param userId 用户ID
     * @return 收藏视频列表
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar " +
            "FROM video v " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "JOIN video_favorite vf ON v.id = vf.video_id " +
            "WHERE vf.user_id = #{userId} AND v.status = 2 " +  // 只查询已发布的视频
            "ORDER BY vf.created_time DESC")  // 按收藏时间倒序排列
    List<VideoVO> selectAllFavoritedVideosByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的播放历史视频列表
     * @param userId 用户ID
     * @return 播放历史视频列表（包含播放进度信息）
     */
    @Select("SELECT v.*, u.user_name as author, u.avatar as avatar, " +
            "h.progress as play_progress, h.play_duration as play_duration, h.updated_time as last_play_time " +
            "FROM user_play_history h " +
            "JOIN video v ON h.video_id = v.id " +
            "LEFT JOIN users u ON v.user_id = u.id " +
            "WHERE h.user_id = #{userId} AND v.status = 2 " +  // 只查询已发布的视频
            "ORDER BY h.updated_time DESC")
    List<VideoVO> selectUserPlayHistory(@Param("userId") Long userId);

    /**
     * 获取用户综合数据统计
     * @param userId 用户ID
     * @return 用户统计数据VO
     */
    @Select("SELECT " +
            "(SELECT COUNT(*) FROM video WHERE user_id = #{userId}) AS videoCount, " +
            "(SELECT COUNT(*) FROM video_favorite WHERE user_id = #{userId}) AS favoriteCount, " +
            "(SELECT COUNT(*) FROM video_like WHERE user_id = #{userId}) AS likeCount, " +
            "(SELECT COUNT(*) FROM user_play_history WHERE user_id = #{userId}) AS historyCount")
    VideoCountDTO selectUserStatistics(@Param("userId") Long userId);

    /**
     * 获取用户视频的综合数据统计（投稿量、总播放量、获赞数、评论数、收藏数）
     * @param userId 用户ID
     * @return 用户视频数据统计DTO
     */
    @Select("SELECT " +
            "(SELECT COUNT(*) FROM video WHERE user_id = #{userId}) AS videoCount, " +
            "(SELECT IFNULL(SUM(play_count), 0) FROM video WHERE user_id = #{userId}) AS playCount, " +
            "(SELECT IFNULL(SUM(like_count), 0) FROM video WHERE user_id = #{userId}) AS likeCount, " +
            "(SELECT IFNULL(SUM(favorite_count), 0) FROM video WHERE user_id = #{userId}) AS favoriteCount, " +
            "(SELECT IFNULL(SUM(comment_count), 0) FROM video WHERE user_id = #{userId}) AS commentCount, " +
            "(SELECT IFNULL(SUM(share_count), 0) FROM video WHERE user_id = #{userId}) AS shareCount")
    UserVideoDataDTO selectUserVideoDataStatistics(@Param("userId") Long userId);


    /**
     * 获取平台整体统计数据
     * @return 平台数据DTO，包含总播放量、总视频数、总点赞数等
     */
    @Select("SELECT " +
            "(SELECT IFNULL(SUM(play_count), 0) FROM video) AS totalPlayCount, " +
            "(SELECT COUNT(*) FROM video) AS totalVideoCount, " +
            "(SELECT IFNULL(SUM(like_count), 0) FROM video) AS totalLikeCount, " +
            "(SELECT IFNULL(SUM(favorite_count), 0) FROM video) AS totalFavoriteCount, " +
            "(SELECT IFNULL(SUM(share_count), 0) FROM video) AS totalShareCount, " +
            "(SELECT IFNULL(SUM(comment_count), 0) FROM video) AS totalCommentCount, " +
            "(SELECT COUNT(*) FROM users) AS totalUserCount, " +
            "(SELECT COUNT(*) FROM video WHERE status = 1) AS totalAuditCount")
    PlatformDataDTO selectPlatformStatistics();

    /**
     * 获取最近一周内每日的综合统计数据（合并所有指标）
     * @return 每日综合统计数据列表
     */
    @Select("SELECT " +
            "    d.date, " +
            "    IFNULL(v.playCount, 0) AS playCount, " +
            "    IFNULL(v.favoriteCount, 0) AS favoriteCount, " +
            "    IFNULL(v.commentCount, 0) AS commentCount, " +
            "    IFNULL(v.likeCount, 0) AS likeCount, " +
            "    IFNULL(v.shareCount, 0) AS shareCount, " +
            "    IFNULL(u.newUserCount, 0) AS newUserCount, " +
            "    IFNULL(v.videoCount, 0) AS videoCount " +  // 新增投稿量
            "FROM " +
            "    (SELECT DATE_SUB(CURDATE(), INTERVAL n DAY) AS date " +
            "     FROM (SELECT 0 AS n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 " +
            "           UNION SELECT 4 UNION SELECT 5 UNION SELECT 6) AS numbers) d " +
            "LEFT JOIN " +
            "    (SELECT DATE(created_time) AS videoDate, " +
            "            SUM(play_count) AS playCount, " +
            "            SUM(favorite_count) AS favoriteCount, " +
            "            SUM(comment_count) AS commentCount, " +
            "            SUM(like_count) AS likeCount, " +
            "            SUM(share_count) AS shareCount, " +
            "            COUNT(*) AS videoCount " +  // 新增投稿量
            "     FROM video " +
            "     WHERE created_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "     GROUP BY DATE(created_time)) v ON d.date = v.videoDate " +
            "LEFT JOIN " +
            "    (SELECT DATE(created_time) AS userDate, COUNT(*) AS newUserCount " +
            "     FROM users " +
            "     WHERE created_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "     GROUP BY DATE(created_time)) u ON d.date = u.userDate " +
            "ORDER BY d.date")
    List<DailyStatisticsDTO> selectDailyAllStatisticsLastWeek();
}
