package com.xingzhi.shortvideosharingplatform.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 视频推荐表
 * </p>
 *
 * @author zjw
 * @since 2025-06-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("video_recommend")
public class VideoRecommend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 推荐记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 视频ID
     */
    private Long videoId;

    /**
     * 推荐类型：1-基于内容(标签和分类)、2-基于热度(如播放量、点赞数、评论数)、3-基于用户行为
     */
    private Integer recommendType;

    /**
     * 推荐得分（用于排序）
     */
    private BigDecimal score;

    /**
     * 推荐生成时间
     */
    private LocalDateTime createTime;

    /**
     * 推荐过期时间（可选，控制时效性）
     */
    private LocalDateTime expireTime;

    /**
     * 是否删除：0-正常、1-删除
     */
    private Integer status;


}
