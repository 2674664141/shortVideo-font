package com.xingzhi.shortvideosharingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingzhi.shortvideosharingplatform.model.UserLoginLogs;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoginLogsMapper extends BaseMapper<UserLoginLogs> {
    
    //插入登录日志(用户ID、登录类型、登录IP、用户代理信息、设备信息、登录状态、登录失败原因)
    @Insert("insert into user_login_logs(user_id, login_type, login_ip, user_agent, device_info, status, fail_reason) " +
            "values(#{userId}, #{loginType}, #{loginIp}, #{userAgent}, #{deviceInfo}, #{status}, #{failReason})")
    void insertLoginLog(Long userId, Integer loginType, String loginIp, String userAgent, 
                       String deviceInfo, Integer status, String failReason);
}
