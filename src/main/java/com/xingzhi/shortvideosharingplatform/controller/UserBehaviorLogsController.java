package com.xingzhi.shortvideosharingplatform.controller;


import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.entity.UserBehaviorLogs;
import com.xingzhi.shortvideosharingplatform.service.IUserBehaviorLogsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 用户行为日志 前端控制器
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@RestController
@RequestMapping("/user-behavior-logs")
public class UserBehaviorLogsController {
    @Resource
    private IUserBehaviorLogsService userBehaviorLogsService;
    /**
     * 记录用户行为并更新
     */
    @PostMapping("/behavior")
    public Result recordUserBehavior(@RequestBody UserBehaviorLogs request) {
        userBehaviorLogsService.save(request);
        return Result.success(200, "行为记录成功");
    }
}
