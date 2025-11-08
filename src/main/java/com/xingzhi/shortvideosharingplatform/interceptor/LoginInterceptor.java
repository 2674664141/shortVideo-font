package com.xingzhi.shortvideosharingplatform.interceptor;

import com.xingzhi.shortvideosharingplatform.properties.JwtProperties;
import com.xingzhi.shortvideosharingplatform.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.annotation.Resource;

//登录拦截器
@Component
public class LoginInterceptor implements HandlerInterceptor{

    @Resource
    private JwtProperties jwtProperties;

    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // 令牌验证
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        String token = request.getHeader("Authorization");

        // 如果没有token，返回401
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        
        // 如果token以"Bearer "开头，去掉前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        //验证token
        try{
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);

            // 将用户信息存储到请求中，供后续使用
            request.setAttribute("userId", claims.get("id"));
            request.setAttribute("username", claims.get("username"));
            //放行
            return true;
        }catch(Exception e){
            response.setStatus(401);
            //不放行
            return false;
        }
    }
}
