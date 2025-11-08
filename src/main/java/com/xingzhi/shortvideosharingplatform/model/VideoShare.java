package com.xingzhi.shortvideosharingplatform.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video_share")
public class VideoShare {

    private Long id;

    private Long userId;

    private Long videoId;

    private Integer shareType;

    private String shareCode;

    private LocalDateTime createdTime;

}
