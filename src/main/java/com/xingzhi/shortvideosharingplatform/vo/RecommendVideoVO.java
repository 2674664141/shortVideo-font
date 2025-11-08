package com.xingzhi.shortvideosharingplatform.vo;

import lombok.Data;

/**
 * 推荐视频VO，用于内部计算和排序
 */
@Data
public class RecommendVideoVO {
    private Long videoId;
    private double similarity;

    public RecommendVideoVO(Long videoId, double similarity) {
        this.videoId = videoId;
        this.similarity = similarity;
    }
}