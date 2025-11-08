package com.xingzhi.shortvideosharingplatform.dto;

import lombok.Data;

@Data
public class SignUpDTO {

    private String userName;

    private String password;

    private String repeatPassword;

    private String phone;

    private String phoneCode;

}
