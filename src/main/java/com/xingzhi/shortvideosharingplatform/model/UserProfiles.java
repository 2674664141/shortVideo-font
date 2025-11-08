package com.xingzhi.shortvideosharingplatform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_profiles")
public class UserProfiles {

  @TableId(type = IdType.AUTO)
  private Long id;//主键ID
  private Long userId;//用户ID
  private String realName;//真实姓名
  private String idCard;//身份证号
  private Long isVerified;//是否实名认证：0-未认证 1-已认证 2-认证失败
  private LocalDateTime verifyTime;//认证时间
  private Integer followerCount;//粉丝数
  private Integer followingCount;//关注数
  private Integer mutualFollowCount;//互相关注数
  private Integer videoCount;//视频数
  private Integer likeCount;//获赞数
  private Integer viewCount;//播放量
  private Integer commentCount;//评论数
  private Integer shareCount;//分享数
  private Integer level;//用户等级
  private Integer experience;//经验值
  private Double balance;//账户余额
  private Double totalRecharge;//累计充值
  private LocalDateTime createdTime;//创建时间
  private LocalDateTime updatedTime;//更新时间
}
