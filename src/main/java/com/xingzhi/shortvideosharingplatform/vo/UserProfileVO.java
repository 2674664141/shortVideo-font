package com.xingzhi.shortvideosharingplatform.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileVO {

    private Long id;

    private Long userId;

    private String realName;

    private String idCard;

    private Integer isVerified;

    private LocalDateTime verifyTime;

    private Integer followerCount;

    private Integer followingCount;

    private Integer videoCount;

    private Integer likeCount;

    private Integer viewCount;

    private Integer commentCount;

    private Integer shareCount;

    private Integer level;

}
