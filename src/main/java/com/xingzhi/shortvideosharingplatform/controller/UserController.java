package com.xingzhi.shortvideosharingplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.dto.SignUpDTO;
import com.xingzhi.shortvideosharingplatform.dto.UserDTO;
import com.xingzhi.shortvideosharingplatform.dto.UserInfoDTO;
import com.xingzhi.shortvideosharingplatform.model.User;
import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.service.UserService;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import com.xingzhi.shortvideosharingplatform.utils.PasswordUtil;
import com.xingzhi.shortvideosharingplatform.vo.UserInfoVO;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.xingzhi.shortvideosharingplatform.service.*;
import com.xingzhi.shortvideosharingplatform.utils.IpUtil;
import com.xingzhi.shortvideosharingplatform.model.*;
import com.xingzhi.shortvideosharingplatform.mapper.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private UserProfilesService userProfilesService;

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private SmsService smsService;

    @Resource
    private UserMapper userMapper;

    private HashMap<String, String> phoneCodeMap = new HashMap<>();

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询是否有该用户，用户可以使用用户名或手机号或邮箱加上密码进行登录
        lambdaQueryWrapper.eq(User::getUserName, user.getAccount())
                .or()
                .eq(User::getPhone, user.getAccount())
                .or()
                .eq(User::getEmail, user.getAccount());
        User loginUser = userService.getOne(lambdaQueryWrapper);
        if (loginUser == null) {
            return Result.error(null, 400, "用户不存在");
        }
        if (!PasswordUtil.checkPassword(user.getPassword(), loginUser.getPassword())) {
            return Result.error(401, "密码错误");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        return Result.success(token);
    }

    @PostMapping("/edit")
    public Result<String> editInfo(HttpServletRequest request, @RequestBody UserInfoDTO userInfoDTO) {
        Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userId);
        User user = userService.getOne(queryWrapper);
        user.setAvatar(userInfoDTO.getAvatar());
        user.setBirthday(userInfoDTO.getBirthday());
        user.setSignature(userInfoDTO.getSignature());
        user.setUserName(userInfoDTO.getUserName());
        user.setGender(userInfoDTO.getGender());
        userService.updateById(user);
        return Result.success("修改成功");
    }

    @GetMapping("/info")
    public Result<UserInfoVO> info(HttpServletRequest request) {
        try {
            String token = request.getHeader(jwtProperties.getUserTokenName());
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = claims.get("userId", Long.class);
            UserInfoVO userInfoVO = userService.selectUserInfoById(userId);
            return Result.success(userInfoVO);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(null, 401, "用户未登录");
        }
    }

    @GetMapping("/sendCode")
    public Result<Boolean> sendCode(@RequestParam String phone) {
        phoneCodeMap.put(phone, "000000");
        return Result.success(true);
    }

    @PostMapping("/signup")
    public Result<Boolean> signup(@RequestBody SignUpDTO userInfo) {
        if (!phoneCodeMap.get(userInfo.getPhone()).equals(userInfo.getPhoneCode())) {
            return Result.error(500, "验证码错误");
        }
        User user = new User();
        user.setUserName(userInfo.getUserName());
        user.setAvatar("http://localhost:3000/image/default-avatar.png");
        user.setGender(0);
        user.setPhoneVerified(1);
        user.setPhone(userInfo.getPhone());
        user.setPassword(PasswordUtil.hashPassword(userInfo.getPassword()));
        userService.save(user);
        phoneCodeMap.remove(userInfo.getPhone());
        return Result.success(true);
    }

    //注册
    @PostMapping("/register")
    public Result register(
            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含英文字母、数字和下划线，长度3-20个字符")
            String username,

            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密码只能包含英文字母、数字和下划线，长度6-20个字符")
            String password,

            @RequestParam
            @Size(min = 1, max = 30, message = "昵称长度必须在1-30个字符之间")
            String nickname,

            HttpServletRequest request){

        //查询用户
        Users user = userService.findByUserName(username);
        if (user == null) {
            //没被占用，注册
            userService.register(username, password, nickname);

            // 注册成功后，查询新注册的用户信息
            Users newUser = userService.findByUserName(username);

            // 获取客户端IP和User-Agent，记录登录日志
            String loginIp = IpUtil.getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            // 更新最后登录时间和IP
            userMapper.updateLastLogin(newUser.getId(), loginIp);

            // 记录登录日志
            userService.recordLoginLog(newUser.getId(), 1, loginIp, userAgent, "", 1, "注册后自动登录");

            // 生成JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", newUser.getId());
            claims.put("username", newUser.getUsername());
            String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

            // 返回登录信息
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", getUserInfo(newUser));

            return Result.success(data, "注册成功，已自动登录");
        } else {
            return Result.error(400, "用户名已被占用");
        }
    }

    //发送验证码
    @PostMapping("/sendPhoneCode")
    public Result sendPhoneCode(
            @RequestParam
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            String phone) {

        // 检查手机号是否已被使用
        Users existUser = userService.findByPhone(phone);
        if (existUser != null) {
            return Result.error(400, "该手机号已被绑定");
        }

        // 发送验证码
        boolean success = smsService.sendVerificationCode(phone);
        if (success) {
            return Result.success(null, "验证码发送成功");
        } else {
            return Result.error(400, "验证码发送失败，请稍后重试");
        }
    }

    //绑定手机号
    @PostMapping("/bindPhone")
    public Result bindPhone(
            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名格式不正确")
            String username,

            @RequestParam
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
            String phone,

            @RequestParam
            @Pattern(regexp = "^\\d{4}$", message = "验证码必须是4位数字")
            String code) {

        // 验证用户是否存在
        Users user = userService.findByUserName(username);
        if (user == null) {
            return Result.error(400, "用户不存在");
        }

        // 检查用户是否已绑定手机号
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            return Result.error(400, "该用户已绑定手机号");
        }

        // 检查手机号是否已被其他用户使用
        Users existUser = userService.findByPhone(phone);
        if (existUser != null) {
            return Result.error(400, "该手机号已被其他用户绑定");
        }

        // 验证短信验证码
        boolean codeValid = smsService.verifyCode(phone, code);
        if (!codeValid) {
            return Result.error(400, "验证码错误或已过期");
        }

        // 绑定手机号
        boolean bindSuccess = userService.bindPhone(username, phone);
        if (bindSuccess) {
            return Result.success(null, "手机号绑定成功");
        } else {
            return Result.error(500, "绑定失败，请重试");
        }
    }

    //用户名密码登录
    @PostMapping("/loginByUsername")
    public Result loginByUsername(
            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含英文字母、数字和下划线，长度3-20个字符")
            String username,

            @RequestParam
            @Pattern(regexp = "^[a-zA-Z0-9_]{6,20}$", message = "密码只能包含英文字母、数字和下划线，长度6-20个字符")
            String password,

            HttpServletRequest request){

        // 获取客户端IP和User-Agent
        String loginIp = IpUtil.getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // 调用业务层登录方法
        Users user = userService.loginByUsername(username, password, loginIp, userAgent);

        if (user != null) {
            // 登录成功，生成JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", getUserInfo(user));

            return Result.success(data, "登录成功");
        } else {
            return Result.error(400, "用户名或密码错误");
        }
    }

    // 获取用户基本信息的辅助方法
    private Map<String, Object> getUserInfo(Users user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        return userInfo;
    }
}

