package com.xingzhi.shortvideosharingplatform.model;

import lombok.Data;

@Data
public class Users {

  private long id;//用户ID
  private String username;//用户名
  private String password;//加密密码
  private String phone;//手机号
  private String email;//邮箱
  private long phoneVerified;//手机号是否已验证：0-未验证 1-已验证
  private long emailVerified;//邮箱是否已验证：0-未验证 1-已验证
  private String nickname;//昵称
  private String avatar;//头像URL
  private long gender;//性别：0-未知 1-男 2-女
  private java.sql.Date birthday;//生日
  private String region;//地区
  private String signature;//个性签名
  private long status;//账号状态：0-注销 1-正常 2-冻结 3-封禁
  private long loginFailedCount;//登录失败次数
  private java.sql.Timestamp lockedUntil;//账号锁定到期时间
  private java.sql.Timestamp lastLoginTime;//最后登录时间
  private String lastLoginIp;//最后登录IP
  private long registerSource;//注册来源：1-手机号 2-邮箱 3-微信 4-QQ 5-微博
  private java.sql.Timestamp createdTime;//创建时间
  private java.sql.Timestamp updatedTime;//更新时间
}
