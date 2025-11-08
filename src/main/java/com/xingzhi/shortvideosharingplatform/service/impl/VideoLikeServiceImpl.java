package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.VideoLikeMapper;
import com.xingzhi.shortvideosharingplatform.model.VideoLike;
import com.xingzhi.shortvideosharingplatform.service.VideoLikeService;
import org.springframework.stereotype.Service;

@Service
public class VideoLikeServiceImpl extends ServiceImpl<VideoLikeMapper, VideoLike> implements VideoLikeService {
}
