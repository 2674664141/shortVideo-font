package com.xingzhi.shortvideosharingplatform.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.dto.AdminDTO;
import com.xingzhi.shortvideosharingplatform.dto.DailyStatisticsDTO;
import com.xingzhi.shortvideosharingplatform.dto.LoginResponse;
import com.xingzhi.shortvideosharingplatform.dto.PlatformDataDTO;
import com.xingzhi.shortvideosharingplatform.entity.Video;
import com.xingzhi.shortvideosharingplatform.model.AdminUser;
import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.service.AdminUserService;
import com.xingzhi.shortvideosharingplatform.service.IVideoService;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import com.xingzhi.shortvideosharingplatform.utils.PasswordUtil;
import com.xingzhi.shortvideosharingplatform.vo.VideoVO;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private IVideoService videoService;

    @PostMapping("/login")
    public Result<LoginResponse> adminLogin(@RequestBody AdminUser adminUser) {
        try {
            String userName = adminUser.getUserName();
            String password = adminUser.getPassword();
            if (userName == null || password == null) {
                return Result.error(401, "用户名或密码不能为空");
            }
            LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AdminUser::getUserName, userName);
            AdminUser user = adminUserService.getOne(queryWrapper);
            if (user == null) {
                return Result.error(401, "用户不存在");
            }
            if (!PasswordUtil.checkPassword(password, user.getPassword())) {
                return Result.error(401, "密码错误");
            }
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getId());
            String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getAdminTtl(), claims);
            return Result.success(new LoginResponse(token, token));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(504, "发生了未知错误");
        }
    }

    @GetMapping("/info")
    public Result<AdminDTO> adminInfo(HttpServletRequest request) {
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
        Long userId = claims.get("userId", Long.class);
        AdminUser adminUser = adminUserService.getById(userId);
        AdminDTO info = new AdminDTO();
        info.setUserId(adminUser.getId());
        info.setUserName(adminUser.getUserName());
        List<String> roles = new ArrayList<>();
        roles.add("R_SUPER");
        info.setRoles(roles);
        List<String> buttons = new ArrayList<>();
        buttons.add("B_CODE1");
        buttons.add("B_CODE2");
        buttons.add("B_CODE3");
        info.setButtons(buttons);
        return Result.success(info);
    }

    @GetMapping("/list")
    public Result<List<AdminUser>> adminList(
            @RequestParam(value = "query", defaultValue = "") String query,
            @RequestParam(value = "pagenum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(AdminUser::getUserName, query);
        Page<AdminUser> page = new Page<>(pageNum, pageSize);
        Page<AdminUser> adminUserPage = adminUserService.page(page, queryWrapper);
        return Result.success(adminUserPage.getRecords());
    }

    @GetMapping("/shenhe/list")
    public Result<List<VideoVO>> shenheList(@RequestParam Integer offset, @RequestParam Integer pageSize) {
        List<VideoVO> videoVOS = videoService.selectAllShenHeVideoList(offset, pageSize);
        return Result.success(videoVOS);
    }

    @GetMapping("/shenhe/count")
    public Result<Integer> shenheCount() {
        return Result.success(videoService.selectTotalShenHeVideoCount());
    }

    @PostMapping("/shenhe/pass")
    public Result<Boolean> videoPass(@RequestBody Map<String, Object> map) {
        System.out.println(map);
        Video video = videoService.getById((Serializable) map.get("id"));
        video.setStatus(2);
        boolean b = videoService.updateById(video);
        return Result.success(b);
    }

    @GetMapping("/platform/data")
    public Result<PlatformDataDTO> platformData() {
        PlatformDataDTO platformDataDTO = videoService.selectPlatformStatistics();
        return Result.success(platformDataDTO);
    }

    @GetMapping("/platform/week")
    public Result<List<DailyStatisticsDTO>> weekData() {
        List<DailyStatisticsDTO> dailyStatisticsDTOS = videoService.selectDailyAllStatisticsLastWeek();
        return Result.success(dailyStatisticsDTOS);
    }

}
