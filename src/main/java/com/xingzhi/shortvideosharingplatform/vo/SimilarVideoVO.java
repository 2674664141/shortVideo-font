package com.xingzhi.shortvideosharingplatform.vo;

import lombok.Data;

@Data
public class SimilarVideoVO {

    private final Long videoId;
    private final double similarity;

    public SimilarVideoVO(Long videoId, double similarity) {
        this.videoId = videoId;
        this.similarity = similarity;
    }

    public Long getVideoId() {
        return videoId;
    }

    public double getSimilarity() {
        return similarity;
    }
}

