package com.xingzhi.shortvideosharingplatform.vo;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoVO {

    private Long id;

    private String userName;

    private String phone;

    private String email;

    private Integer phoneVerified;

    private Integer emailVerified;

    private String nickname;

    private String avatar;

    private Integer gender;

    private String region;

    private LocalDateTime birthday;

    private String signature;

    private Integer status;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

    private UserProfileVO userProfile;

    private UserPrivacyVO userPrivacy;

}
