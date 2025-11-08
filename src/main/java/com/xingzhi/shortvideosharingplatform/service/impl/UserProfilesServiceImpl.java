package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.UserProfilesMapper;
import com.xingzhi.shortvideosharingplatform.model.UserProfiles;
import com.xingzhi.shortvideosharingplatform.service.UserProfilesService;
import org.springframework.stereotype.Service;

@Service
public class UserProfilesServiceImpl extends ServiceImpl<UserProfilesMapper, UserProfiles> implements UserProfilesService {
}
