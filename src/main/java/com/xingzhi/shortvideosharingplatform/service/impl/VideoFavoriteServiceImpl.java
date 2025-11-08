package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.VideoFavoriteMapper;
import com.xingzhi.shortvideosharingplatform.model.VideoFavorite;
import com.xingzhi.shortvideosharingplatform.service.VideoFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class VideoFavoriteServiceImpl extends ServiceImpl<VideoFavoriteMapper, VideoFavorite> implements VideoFavoriteService {
}
