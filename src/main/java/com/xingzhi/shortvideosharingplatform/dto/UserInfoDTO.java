package com.xingzhi.shortvideosharingplatform.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserInfoDTO {

    private Long id;

    private String userName;

    private String email;

    private String phone;

    private String nickName;

    private String avatar;

    private Integer gender;

    private String region;

    private String signature;

    private Integer status;

    private LocalDateTime birthday;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
