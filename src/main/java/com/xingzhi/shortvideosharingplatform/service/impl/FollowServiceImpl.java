package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.dto.FollowDTO;
import com.xingzhi.shortvideosharingplatform.mapper.FollowMapper;
import com.xingzhi.shortvideosharingplatform.model.Follow;
import com.xingzhi.shortvideosharingplatform.service.FollowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Resource
    private FollowMapper followMapper;

    @Override
    public List<FollowDTO> selectFollowers(Long userId) {
        return followMapper.selectFollowers(userId);
    }

    @Override
    public List<FollowDTO> selectFollowings(Long userId) {
        return followMapper.selectFollowings(userId);
    }
}
