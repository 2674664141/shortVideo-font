package com.xingzhi.shortvideosharingplatform.model;

import lombok.Data;

@Data
public class UserLoginLogs {

  private Long id;//主键ID
  private Long userId;//用户ID
  private Long loginType;//登录类型：1-用户名密码 2-手机验证码 3-第三方登录
  private String loginIp;//登录IP
  private String userAgent;//用户代理信息
  private String deviceInfo;//设备信息
  private String location;//登录地点
  private Long status;//登录状态：0-失败 1-成功
  private String failReason;//失败原因
  private java.sql.Timestamp loginTime;//登录时间
}
