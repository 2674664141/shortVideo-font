package com.xingzhi.shortvideosharingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingzhi.shortvideosharingplatform.model.User;
import com.xingzhi.shortvideosharingplatform.vo.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.xingzhi.shortvideosharingplatform.model.Users;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    UserInfoVO selectUserInfoById(@Param("userId") Long userId);

    //根据用户名查找用户
    @Select("select * from users where username=#{username}")
    Users findByUserName(String username);

    //根据手机号查找用户
    @Select("select * from users where phone=#{phone}")
    Users findByPhone(String phone);

    //插入基本信息（用户名、密码、昵称）
    @Insert("insert into users(username, password, nickname, status, register_source) " +
            "values(#{username}, #{password}, #{nickname}, 1, 1)")
    void addInfo(String username, String password, String nickname);

    //更改手机号
    @Update("update users set phone=#{phone}, phone_verified=1 where username=#{username}")
    void updatePhone(String username, String phone);

    //更新用户最后登录时间和IP
    @Update("update users set last_login_time=NOW(), last_login_ip=#{loginIp} where id=#{userId}")
    void updateLastLogin(Long userId, String loginIp);

}
