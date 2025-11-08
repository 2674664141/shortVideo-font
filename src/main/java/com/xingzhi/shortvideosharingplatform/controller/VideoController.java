package com.xingzhi.shortvideosharingplatform.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.dto.UserVideoDataDTO;
import com.xingzhi.shortvideosharingplatform.dto.VideoCountDTO;
import com.xingzhi.shortvideosharingplatform.dto.VideoFavoriteDTO;
import com.xingzhi.shortvideosharingplatform.dto.VideoLikeDTO;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.xingzhi.shortvideosharingplatform.model.UserPlayHistory;
import com.xingzhi.shortvideosharingplatform.model.VideoFavorite;
import com.xingzhi.shortvideosharingplatform.model.VideoLike;
import com.xingzhi.shortvideosharingplatform.model.VideoShare;
import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.service.*;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import com.xingzhi.shortvideosharingplatform.vo.VideoVO;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频信息表 前端控制器
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/video")
public class VideoController {
    @Resource
    private IVideoService videoService;

    @Resource
    private VideoLikeService videoLikeService;

    @Resource
    private VideoShareService videoShareService;

    @Resource
    private VideoFavoriteService videoFavoriteService;

    @Resource
    private UserPlayHistoryService playHistoryService;

    //根据用户id获取视频列表 发布数量

    @Resource
    private JwtProperties jwtProperties;

    @GetMapping("/user/list")
    public Result<List<VideoVO>> list(HttpServletRequest request) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        List<VideoVO> videoVOS = videoService.selectVideosByUserId(userId);
        return Result.success(videoVOS);
    }

    @GetMapping("/user/data")
    public Result<UserVideoDataDTO> userVideoData(HttpServletRequest request) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        UserVideoDataDTO dataDTO = videoService.selectUserVideoDataStatistics(userId);
        return Result.success(dataDTO);
    }

    @GetMapping("/user/tab")
    public Result<List<VideoVO>> tabList(HttpServletRequest request, @RequestParam String tab) {
        try {
            Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
            if (tab.equals("post")) {
                List<VideoVO> videoVOS = videoService.selectVideosByUserId(userId);
                return Result.success(videoVOS);
            }
            if (tab.equals("like")) {
                List<VideoVO> videoVOS = videoService.selectAllLikedVideosByUserId(userId);
                return Result.success(videoVOS);
            }
            if (tab.equals("collect")) {
                List<VideoVO> videoVOS = videoService.selectAllFavoritedVideosByUserId(userId);
                return Result.success(videoVOS);
            }
            if (tab.equals("history")) {
                List<VideoVO> videoVOS = videoService.selectUserPlayHistory(userId);
                return Result.success(videoVOS);
            }
            return Result.error(null, 400, "参数错误");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(null, 401, "用户未登录");
        }
    }

    //根据分类id获取这个系列视频
    @GetMapping("/listByCategory/{categoryId}")
    public Result<List<Video>> GetVideoListByCategory(@PathVariable("categoryId")long categoryId) {
        List<Video> videoList = videoService.list(new QueryWrapper<Video>().eq("category_id", categoryId));
        return Result.success(videoList);
    }

    @GetMapping("/jingxuan")
    public Result<List<VideoVO>> list(@RequestParam Integer offset, @RequestParam Integer pageSize) {
        List<VideoVO> videoVOList = videoService.selectVideoList(offset, pageSize);
        return Result.success(videoVOList);
    }


    @PostMapping("/tougao")
    public Result<Video> tougao(HttpServletRequest request, @RequestBody Video video) {
        String token = request.getHeader(jwtProperties.getUserTokenName());
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
        Long userId = claims.get("userId", Long.class);
        video.setUserId(userId);
        videoService.save(video);
        return Result.success(video);
    }

    @GetMapping("/info/{id}")
    public Result<VideoVO> getVideoById(@PathVariable String id) {
        VideoVO videoVO = videoService.getVideoById(Long.valueOf(id));
        return Result.success(videoVO);
    }

    @GetMapping("/interaction/{id}")
    public Result<Map<String, Boolean>> getInteraction(HttpServletRequest request, @PathVariable Long id) {
        String token = request.getHeader(jwtProperties.getUserTokenName());
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
        Long userId = claims.get("userId", Long.class);
        Map<String, Boolean> status = videoService.selectUserInteractionStatus(id, userId);
        return Result.success(status);
    }

    @GetMapping("/tuijian")
    public Result<List<VideoVO>> getTuiJianVideo(@RequestParam Integer offset, @RequestParam Integer pageSize) {
        List<VideoVO> videoVOS = videoService.selectVideoList(offset, pageSize);
        return Result.success(videoVOS);
    }

    @GetMapping("/category/{id}")
    public Result<List<VideoVO>> getVideoByCategory(@PathVariable Integer id) {
        List<VideoVO> videoVOS = videoService.getVideoByCategory(id, 0, 18);
        return Result.success(videoVOS);
    }

    @PostMapping("/like/{id}")
    public Result<VideoLikeDTO> like(HttpServletRequest request, @PathVariable String id) {
        try {
            String token = request.getHeader(jwtProperties.getUserTokenName());
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = claims.get("userId", Long.class);
            LambdaQueryWrapper<VideoLike> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VideoLike::getUserId, userId).eq(VideoLike::getVideoId, id);
            VideoLike like = videoLikeService.getOne(queryWrapper);
            if (like == null) {
                VideoLike videoLike = new VideoLike();
                videoLike.setVideoId(Long.valueOf(id));
                videoLike.setUserId(userId);
                videoLikeService.save(videoLike);
                return Result.success(new VideoLikeDTO(true, "点赞成功", Long.valueOf(id)));
            } else {
                // 取消点赞
                videoLikeService.removeById(like);
                return Result.success(new VideoLikeDTO(false, "取消点赞成功", Long.valueOf(id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "点赞失败");
        }
    }

    @PostMapping("/favorite/{id}")
    public Result<VideoFavoriteDTO> favorite(HttpServletRequest request, @PathVariable String id) {
        try {
            Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
            LambdaQueryWrapper<VideoFavorite> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VideoFavorite::getUserId, userId).eq(VideoFavorite::getVideoId, id);
            VideoFavorite favorite = videoFavoriteService.getOne(queryWrapper);
            if (favorite == null) {
                VideoFavorite f = new VideoFavorite();
                f.setVideoId(Long.valueOf(id));
                f.setUserId(userId);
                videoFavoriteService.save(f);
                return Result.success(new VideoFavoriteDTO(true, "收藏成功", Long.valueOf(id)));
            } else {
                videoFavoriteService.removeById(favorite);
                return Result.success(new VideoFavoriteDTO(false, "取消收藏成功", Long.valueOf(id)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "收藏失败");
        }

    }


    @PostMapping("/play/{id}")
    public Result<UserPlayHistory> play(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        LambdaQueryWrapper<UserPlayHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPlayHistory::getUserId, userId).eq(UserPlayHistory::getVideoId, id);
        UserPlayHistory history = playHistoryService.getOne(queryWrapper);
        if (history == null) {
            UserPlayHistory userPlayHistory = new UserPlayHistory();
            userPlayHistory.setUserId(userId);
            userPlayHistory.setVideoId(id);
            playHistoryService.save(userPlayHistory);
        } else {
            history.setUpdatedTime(LocalDateTime.now());
            playHistoryService.updateById(history);
        }
        return Result.success(history);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean b = videoService.removeById(id);
        return Result.success(b);
    }

    @GetMapping("/count")
    public Result<VideoCountDTO> count(HttpServletRequest request) {
        try {
            Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
            VideoCountDTO countDTO = videoService.selectUserStatistics(userId);
            return Result.success(countDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(504, "获取失败");
        }
    }

    @GetMapping("/search")
    public Result<List<VideoVO>> searchVideoList(@RequestParam String keyword) {
        if (keyword == null) {
            keyword = "";
        }
        List<VideoVO> videoVOS = videoService.searchVideos(keyword, 0, 15);
        return Result.success(videoVOS);
    }

    @PostMapping("/share/{id}")
    public Result<String> share(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        VideoShare videoShare = new VideoShare();
        videoShare.setVideoId(id);
        videoShare.setShareType(1);
        videoShare.setUserId(userId);
        videoShare.setShareCode("http://localhost:5173/#/jingxuan?videoId=" + id);
        videoShareService.save(videoShare);
        return Result.success("分享成功");
    }
}
