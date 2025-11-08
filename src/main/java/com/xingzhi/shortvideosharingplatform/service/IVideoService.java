package com.xingzhi.shortvideosharingplatform.service;

import com.xingzhi.shortvideosharingplatform.dto.DailyStatisticsDTO;
import com.xingzhi.shortvideosharingplatform.dto.PlatformDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.UserVideoDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.VideoCountDTO;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingzhi.shortvideosharingplatform.vo.VideoVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频信息表 服务类
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
public interface IVideoService extends IService<Video> {

    List<VideoVO> selectVideoList(Integer offset, Integer pageSize);

    VideoVO getVideoById(Long videoId);

    List<VideoVO> getVideoByCategory(Integer categoryId, Integer offset, Integer pageSize);

    Map<String, Boolean> selectUserInteractionStatus(Long videoId, Long userId);

    List<VideoVO> selectVideosByUserId(Long userId);

    List<VideoVO> selectAllLikedVideosByUserId(Long userId);

    List<VideoVO> selectAllFavoritedVideosByUserId(Long userId);

    List<VideoVO> selectUserPlayHistory(Long userId);

    VideoCountDTO selectUserStatistics(Long userId);

    UserVideoDataDTO selectUserVideoDataStatistics(Long userId);

    List<VideoVO> searchVideos(String keyword, Integer offset, Integer pageSize);

    List<VideoVO> selectAllShenHeVideoList(Integer offset, Integer pageSize);

    Integer selectTotalShenHeVideoCount();

    PlatformDataDTO selectPlatformStatistics();

    List<DailyStatisticsDTO> selectDailyAllStatisticsLastWeek();

}
