package com.xingzhi.shortvideosharingplatform.controller;

import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.dto.CommentDTO;
import com.xingzhi.shortvideosharingplatform.model.Comment;
import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.service.CommentService;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private JwtProperties jwtProperties;

    @GetMapping("/video/{id}")
    public Result<List<CommentDTO>> getCommentsByVideoId(@PathVariable Long id) {
        List<CommentDTO> commentDTOS = commentService.selectVideoCommentList(id);
        return Result.success(commentDTOS);
    }

    @PostMapping("/video")
    public Result<Boolean> addComment(HttpServletRequest request, @RequestBody Comment comment) {
        try {
            Long userId = JwtUtil.getClaimsAttribute(request, jwtProperties.getUserTokenName(), jwtProperties.getUserSecretKey(), "userId", Long.class);
            comment.setUserId(userId);
            boolean save = commentService.save(comment);
            return Result.success(save);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(504, "添加评论失败");
        }
    }

}
