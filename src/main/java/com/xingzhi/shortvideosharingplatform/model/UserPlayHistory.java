package com.xingzhi.shortvideosharingplatform.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_play_history")
public class UserPlayHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long videoId;

    private Integer playDuration;

    private Float progress;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;

}
