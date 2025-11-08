package com.xingzhi.shortvideosharingplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingzhi.shortvideosharingplatform.dto.CommentDTO;
import com.xingzhi.shortvideosharingplatform.model.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    List<CommentDTO> selectVideoCommentList(Long videoId);

}
