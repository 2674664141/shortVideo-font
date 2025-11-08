package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.VideoShareMapper;
import com.xingzhi.shortvideosharingplatform.model.VideoShare;
import com.xingzhi.shortvideosharingplatform.service.VideoShareService;
import org.springframework.stereotype.Service;

@Service
public class VideoShareServiceImpl extends ServiceImpl<VideoShareMapper, VideoShare> implements VideoShareService {
}
