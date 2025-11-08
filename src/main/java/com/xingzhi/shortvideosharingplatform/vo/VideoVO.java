package com.xingzhi.shortvideosharingplatform.vo;

import com.xingzhi.shortvideosharingplatform.model.VideoLike;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoVO {

    private Long id;

    private Long userId;

    private String title;

    private String author;

    private String avatar;

    private VideoLike likeInfo;

    private String description;

    private String videoUrl;

    private String coverUrl;

    private Integer duration;

    private Integer categoryId;

    private String tags;

    private Integer status;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer shareCount;

    private Integer playCount;

    private Integer favoriteCount;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
