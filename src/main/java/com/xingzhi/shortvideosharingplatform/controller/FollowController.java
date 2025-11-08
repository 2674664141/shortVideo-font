package com.xingzhi.shortvideosharingplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.dto.FollowDTO;
import com.xingzhi.shortvideosharingplatform.model.Follow;
import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.service.FollowService;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private FollowService followService;

    @Resource
    private JwtProperties jwtProperties;

    @GetMapping("/followers")
    public Result<List<FollowDTO>> list(HttpServletRequest request) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        List<FollowDTO> followDTOS = followService.selectFollowers(userId);
        return Result.success(followDTOS);
    }

    @GetMapping("/following")
    public Result<List<FollowDTO>> listFollowing(HttpServletRequest request) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        List<FollowDTO> followDTOS = followService.selectFollowings(userId);
        return Result.success(followDTOS);
    }

    @PostMapping("/guanzhu/{id}")
    public Result<Boolean> guanzhu(HttpServletRequest request, @PathVariable Long id) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, userId).eq(Follow::getFollowingId, id);
        Follow follow = followService.getOne(queryWrapper);
        if (follow != null) {
            followService.removeById(follow);
            return Result.success(false);
        } else {
            follow = new Follow();
            follow.setFollowerId(userId);
            follow.setFollowingId(id);
            follow.setIsSpecialFollow(0);
            followService.save(follow);
            return Result.success(true);
        }
    }


}
