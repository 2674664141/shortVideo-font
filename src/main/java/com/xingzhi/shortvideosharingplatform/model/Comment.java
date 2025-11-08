package com.xingzhi.shortvideosharingplatform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    private Long parentId;

    private Long replyToUserId;

    private Integer isDeleted;

    private Integer likeCount;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
