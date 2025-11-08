package com.xingzhi.shortvideosharingplatform.controller;


import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.xingzhi.shortvideosharingplatform.service.IVideoRecommendService;
import com.xingzhi.shortvideosharingplatform.service.IVideoService;
import com.xingzhi.shortvideosharingplatform.vo.HotVideoVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 视频推荐表 前端控制器
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/video-recommend")
public class VideoRecommendController {
    @Resource
    private IVideoRecommendService videoRecommendService;
    @Resource
    private IVideoService videoService;

    /**
     *  基于视频热度 返回视频
     *
     */

    @GetMapping("/baseHot")
    public Result<List<Video>> baseHot() {
        List<HotVideoVO> hotVideoVOList = videoRecommendService.getTopHotVideos();
        if (hotVideoVOList == null) return Result.error(200, "没有数据");
        List<Video> videoList = hotVideoVOList.stream().map(hotVideoVO -> videoService.getById(hotVideoVO.getVideoId())).collect(Collectors.toList());
        return Result.success(videoList);
    }

    /**
     *  基于视频内容 返回视频
     *
     */
    @GetMapping("/baseContent")
    public Result<List<Video>> baseCategory(@RequestParam Long userId) {
        List<Video> videoList = videoRecommendService.recommendByContent(userId);
        return Result.success(videoList);
    }
    /**
     *  基于用户行为 返回视频  换一换
     *
     */
    @GetMapping("/baseUserBehavior")
    public Result<List<Video>> baseUserBehavior(@RequestParam Long userId, @RequestParam boolean refresh) {
        List<Video> videoList = videoRecommendService.recommendByBehavior(userId,refresh);
        return Result.success(videoList);

    }
    @GetMapping("/getVideoDetail")
    public Result<Video> getVideoDetail(@PathVariable ("videoId") Long videoId) {

        Video video = videoService.getById(videoId);
            return Result.success(video);
    }





}
