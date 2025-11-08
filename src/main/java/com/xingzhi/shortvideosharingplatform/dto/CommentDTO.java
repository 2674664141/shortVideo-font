package com.xingzhi.shortvideosharingplatform.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;

    private Long userId;

    private Long videoId;

    private String userName;

    private String avatar;

    private String content;

    private Integer likeCount;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
