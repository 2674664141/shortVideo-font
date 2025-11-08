package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.UserMapper;
import com.xingzhi.shortvideosharingplatform.model.User;
import com.xingzhi.shortvideosharingplatform.service.UserService;
import com.xingzhi.shortvideosharingplatform.vo.UserInfoVO;
import org.springframework.stereotype.Service;
import com.xingzhi.shortvideosharingplatform.mapper.UserLoginLogsMapper;
import com.xingzhi.shortvideosharingplatform.model.Users;
import com.xingzhi.shortvideosharingplatform.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private UserLoginLogsMapper userLoginLogsMapper;

    public UserInfoVO selectUserInfoById(Long userId) {
        return userMapper.selectUserInfoById(userId);
    }

    @Override
    public Users findByUserName(String username){
        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String username, String password, String nickname){
        //加密
        String md5String  = Md5Util.getMD5String(password);
        //添加
        userMapper.addInfo(username, md5String, nickname);
    }

    //根据手机号查找用户
    @Override
    public Users findByPhone(String phone){
        return userMapper.findByPhone(phone);
    }

    //绑定手机号
    @Override
    public boolean bindPhone(String username, String phone){
        try{
            userMapper.updatePhone(username, phone);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    //用户名密码登录
    @Override
    public Users loginByUsername(String username, String password, String loginIp, String userAgent) {
        // 根据用户名查询用户
        Users user = userMapper.findByUserName(username);

        if (user == null) {
            // 用户不存在时，不记录登录日志，或者记录为特殊值
            // recordLoginLog(-1L, 1, loginIp, userAgent, "", 0, "用户不存在");
            return null;
        }

        // 检查账号状态
        if (user.getStatus() != 1) {
            recordLoginLog(user.getId(), 1, loginIp, userAgent, "", 0, "账号已被冻结或封禁");
            return null;
        }

        // 验证密码
        if (!Md5Util.getMD5String(password).equals(user.getPassword())) {
            recordLoginLog(user.getId(), 1, loginIp, userAgent, "", 0, "密码错误");
            return null;
        }

        // 登录成功，更新最后登录时间和IP
        userMapper.updateLastLogin(user.getId(), loginIp);

        // 记录登录成功日志
        recordLoginLog(user.getId(), 1, loginIp, userAgent, "", 1, "");

        return user;
    }

    //记录登录日志
    @Override
    public void recordLoginLog(Long userId, Integer loginType, String loginIp, String userAgent,
                               String deviceInfo, Integer status, String failReason) {
        try {
            userLoginLogsMapper.insertLoginLog(userId, loginType, loginIp, userAgent,
                    deviceInfo, status, failReason);
        } catch (Exception e) {
            // 记录日志失败不应该影响登录流程，只记录异常
            e.printStackTrace();
        }
    }

}
