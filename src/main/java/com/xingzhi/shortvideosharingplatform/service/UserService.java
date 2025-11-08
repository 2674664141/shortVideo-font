package com.xingzhi.shortvideosharingplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingzhi.shortvideosharingplatform.model.User;
import com.xingzhi.shortvideosharingplatform.model.Users;
import com.xingzhi.shortvideosharingplatform.vo.UserInfoVO;

public interface UserService extends IService<User> {

    UserInfoVO selectUserInfoById(Long userId);

    //根据用户名查询用户
    Users findByUserName(String username);

    //注册
    void register(String username, String password, String nickname);

    //根据手机号查找用户
    Users findByPhone(String phone);

    //绑定手机号
    boolean bindPhone(String username, String phone);

    //用户名密码登录
    Users loginByUsername(String username, String password, String loginIp, String userAgent);

    //记录登录日志
    void recordLoginLog(Long userId, Integer loginType, String loginIp, String userAgent,
                        String deviceInfo, Integer status, String failReason);

}

