package com.xingzhi.shortvideosharingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingzhi.shortvideosharingplatform.dto.FollowDTO;
import com.xingzhi.shortvideosharingplatform.model.Follow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    @Select("SELECT f.id, f.follower_id, f.following_id, " +
            "u1.user_name AS followerName, u1.avatar AS followerAvatar, " +
            "u1.signature AS followerSignature, f.created_time, " +
            "CASE WHEN EXISTS ( " +
            "    SELECT 1 FROM user_follow f2 " +
            "    WHERE f2.follower_id = f.following_id " +
            "    AND f2.following_id = f.follower_id " +
            ") THEN TRUE ELSE FALSE END AS isMutualFollow " +
            "FROM user_follow f " +
            "LEFT JOIN users u1 ON f.follower_id = u1.id " +
            "WHERE f.following_id = #{userId} " +
            "ORDER BY f.created_time DESC")
    List<FollowDTO> selectFollowers(@Param("userId") Long userId);

    @Select("SELECT f.id, f.follower_id, f.following_id, " +
            "u2.user_name AS followingName, u2.avatar AS followingAvatar, " +
            "u2.signature AS followingSignature, f.created_time, " +
            "CASE WHEN EXISTS ( " +
            "    SELECT 1 FROM user_follow f2 " +
            "    WHERE f2.follower_id = f.following_id " +
            "    AND f2.following_id = f.follower_id " +
            ") THEN TRUE ELSE FALSE END AS isMutualFollow " +
            "FROM user_follow f " +
            "LEFT JOIN users u2 ON f.following_id = u2.id " +
            "WHERE f.follower_id = #{userId} " +
            "ORDER BY f.created_time DESC")
    List<FollowDTO> selectFollowings(@Param("userId") Long userId);
}
