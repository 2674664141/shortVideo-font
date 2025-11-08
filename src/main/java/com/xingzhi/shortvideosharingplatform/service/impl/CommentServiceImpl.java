package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.dto.CommentDTO;
import com.xingzhi.shortvideosharingplatform.mapper.CommentMapper;
import com.xingzhi.shortvideosharingplatform.model.Comment;
import com.xingzhi.shortvideosharingplatform.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<CommentDTO> selectVideoCommentList(Long videoId) {
        return commentMapper.selectVideoCommentList(videoId);
    }
}
