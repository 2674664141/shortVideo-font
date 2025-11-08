package com.xingzhi.shortvideosharingplatform.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailyStatisticsDTO {
    private LocalDate date;          // 日期

    private Integer playCount;  // 播放量

    private Integer favoriteCount; // 收藏量

    private Integer commentCount; // 评论量

    private Integer likeCount;    // 点赞量

    private Integer shareCount;   // 分享量

    private Integer newUserCount; // 新增用户量

    private Integer videoCount;   // 投稿量

}
