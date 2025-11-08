package com.xingzhi.shortvideosharingplatform.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.xingzhi.shortvideosharingplatform.service.IVideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 视频分类表 前端控制器
 * </p>
 *
 * @author zjw
 * @since 2025-06-20
 */
@RestController
@RequestMapping("/video-category")
public class VideoCategoryController {
    @Resource
    private IVideoService videoService;
    /**
     * 获取分类下的视频 缓存
     * @return
     */
    @GetMapping("/getByCategoryId")
    public Result<List<Video>> getVideosByCategoryId(@PathVariable("id") long id) {
        List<Video> videoList = videoService.list(new QueryWrapper<Video>().eq("category_id", id));
        return Result.success(videoList);
    }


}
