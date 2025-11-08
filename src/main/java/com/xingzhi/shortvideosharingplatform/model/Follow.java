package com.xingzhi.shortvideosharingplatform.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_follow")
public class Follow {

    private Long id;

    private Long followerId;

    private Long followingId;

    private Long groupId;

    private Integer isSpecialFollow;

    private LocalDateTime createdTime;

}
