package com.xingzhi.shortvideosharingplatform.service.impl;

import com.xingzhi.shortvideosharingplatform.dto.DailyStatisticsDTO;
import com.xingzhi.shortvideosharingplatform.dto.PlatformDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.UserVideoDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.VideoCountDTO;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.xingzhi.shortvideosharingplatform.mapper.VideoMapper;
import com.xingzhi.shortvideosharingplatform.service.IVideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.vo.VideoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频信息表 服务实现类
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {

    @Resource
    private VideoMapper videoMapper;

    @Override
    public List<VideoVO> selectVideoList(Integer offset, Integer pageSize) {
        return videoMapper.selectAllVideos(offset, pageSize);
    }

    @Override
    public VideoVO getVideoById(Long videoId) {
        return videoMapper.selectVideoDetailById(videoId);
    }

    @Override
    public List<VideoVO> getVideoByCategory(Integer categoryId, Integer offset, Integer pageSize) {
        return videoMapper.selectVideosByCategoryId(categoryId, offset, pageSize);
    }

    @Override
    public Map<String, Boolean> selectUserInteractionStatus(Long videoId, Long userId) {
        return videoMapper.selectUserInteractionStatus(videoId, userId);
    }

    @Override
    public List<VideoVO> selectVideosByUserId(Long userId) {
        return videoMapper.selectVideosByUserId(userId);
    }

    @Override
    public List<VideoVO> selectAllLikedVideosByUserId(Long userId) {
        return videoMapper.selectAllLikedVideosByUserId(userId);
    }

    @Override
    public List<VideoVO> selectAllFavoritedVideosByUserId(Long userId) {
        return videoMapper.selectAllFavoritedVideosByUserId(userId);
    }

    @Override
    public List<VideoVO> selectUserPlayHistory(Long userId) {
        return videoMapper.selectUserPlayHistory(userId);
    }

    @Override
    public VideoCountDTO selectUserStatistics(Long userId) {
        return videoMapper.selectUserStatistics(userId);
    }

    @Override
    public UserVideoDataDTO selectUserVideoDataStatistics(Long userId) {
        return videoMapper.selectUserVideoDataStatistics(userId);
    }



    @Override
    public List<VideoVO> searchVideos(String keyword, Integer offset, Integer pageSize) {
        return videoMapper.searchVideos(keyword, offset, pageSize);
    }

    @Override
    public List<VideoVO> selectAllShenHeVideoList(Integer offset, Integer pageSize) {
        return videoMapper.selectAllShenHeVideoList(offset, pageSize);
    }

    @Override
    public Integer selectTotalShenHeVideoCount() {
        return videoMapper.selectTotalShenHeVideoCount();
    }

    @Override
    public PlatformDataDTO selectPlatformStatistics() {
        return videoMapper.selectPlatformStatistics();
    }

    @Override
    public List<DailyStatisticsDTO> selectDailyAllStatisticsLastWeek() {
        return videoMapper.selectDailyAllStatisticsLastWeek();
    }


}
