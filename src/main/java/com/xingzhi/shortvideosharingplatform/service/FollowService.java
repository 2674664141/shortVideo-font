package com.xingzhi.shortvideosharingplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingzhi.shortvideosharingplatform.dto.FollowDTO;
import com.xingzhi.shortvideosharingplatform.model.Follow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FollowService extends IService<Follow> {

    List<FollowDTO> selectFollowers(@Param("userId") Long userId);

    List<FollowDTO> selectFollowings(@Param("userId") Long userId);

}
