package com.xingzhi.shortvideosharingplatform.vo;


import lombok.Data;

@Data
public class UserPrivacyVO {

    private Long id;

    private Long userId;

    private Integer accountPublic;

    private Integer allowComment;

    private Integer allowMessage;

    private Integer showViewHistory;

    private Integer allowDownload;

}
