package com.xingzhi.shortvideosharingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingzhi.shortvideosharingplatform.dto.CommentDTO;
import com.xingzhi.shortvideosharingplatform.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT c.*, u.user_name, u.avatar " +
            "FROM comment c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "WHERE c.video_id = #{videoId} " +
            "ORDER BY c.created_time DESC"
            )
    List<CommentDTO> selectVideoCommentList(@Param("videoId") Long videoId);

}
