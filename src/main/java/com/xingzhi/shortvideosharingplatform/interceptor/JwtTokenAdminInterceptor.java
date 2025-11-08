package com.xingzhi.shortvideosharingplatform.interceptor;


import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;


/**
 * 后台管理功能的jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null) {
            //当前请求没有带认证头，返回401
            response.setStatus(401);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的认证头");
            return false;
        }
        if (token.isEmpty()) {
            //当前请求没有带令牌，返回401
            response.setStatus(401);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的认证令牌");
            return false;
        }
        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = claims.get("userId", Long.class);
            log.info("当前用户的id是: {}", userId);
            return true;
        } catch (Exception e) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            e.printStackTrace();
            return false;
        }
    }

}
