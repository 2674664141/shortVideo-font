package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.AdminUserMapper;
import com.xingzhi.shortvideosharingplatform.model.AdminUser;
import com.xingzhi.shortvideosharingplatform.service.AdminUserService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
}
