package com.xingzhi.shortvideosharingplatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowDTO {

    private Long id;

    private Long followerId;

    private Long followingId;

    private String followerName;

    private String followingName;

    private String followerAvatar;

    private String followingAvatar;

    private String followerSignature;

    private String followingSignature;

    private LocalDateTime createdTime;

    private Boolean isMutualFollow; // 新增字段，表示是否互相关注

}
