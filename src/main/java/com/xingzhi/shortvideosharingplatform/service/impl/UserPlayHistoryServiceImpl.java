package com.xingzhi.shortvideosharingplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingzhi.shortvideosharingplatform.mapper.UserPlayHistoryMapper;
import com.xingzhi.shortvideosharingplatform.model.UserPlayHistory;
import com.xingzhi.shortvideosharingplatform.service.UserPlayHistoryService;
import org.springframework.stereotype.Service;

@Service
public class UserPlayHistoryServiceImpl extends ServiceImpl<UserPlayHistoryMapper, UserPlayHistory> implements UserPlayHistoryService {
}
