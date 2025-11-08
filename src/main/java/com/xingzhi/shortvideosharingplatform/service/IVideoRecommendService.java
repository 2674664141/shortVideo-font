package com.xingzhi.shortvideosharingplatform.service;

import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.xingzhi.shortvideosharingplatform.entity.VideoRecommend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xingzhi.shortvideosharingplatform.vo.HotVideoVO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频推荐表 服务类
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
public interface IVideoRecommendService extends IService<VideoRecommend> {

    List<HotVideoVO> getTopHotVideos();

    List<Video> recommendByContent(Long userId);

    List<Video> recommendByBehavior(Long userId,boolean  refresh);
}
