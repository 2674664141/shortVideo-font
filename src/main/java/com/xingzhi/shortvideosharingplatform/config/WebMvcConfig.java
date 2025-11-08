package com.xingzhi.shortvideosharingplatform.config;

import com.xingzhi.shortvideosharingplatform.common.JacksonObjectMapper;
import com.xingzhi.shortvideosharingplatform.interceptor.JwtTokenAdminInterceptor;
import com.xingzhi.shortvideosharingplatform.interceptor.LoginInterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

//import javax.annotation.Resource;
import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

//    @Resource
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始进行拦截器配置...");
        // 拦截器处理的，比如jwt拦截器就在这里添加
//        registry.addInterceptor(jwtTokenAdminInterceptor)
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns("/admin/login");
        // 注册JWT拦截器，拦截所有请求但排除登录注册等接口
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/user/**", "/video/like/**", "/video/favorite/**", "/comment/video")  // 拦截所有请求
                .excludePathPatterns(
                    "/user/register",      // 排除注册接口
                    "/user/loginByUsername", // 排除用户名密码登录
                    "/user/sendPhoneCode",   // 排除发送验证码
                    "/error",               // 排除错误页面
                    "/image/**",            // 排除静态资源
                        "/user/login",
                        "/user/sendCode",
                        "/user/signup"
                );
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        // 处理静态资源的，比如这里是映射到resources下的image文件夹下的所有文件，也就是resources下的image文件夹下的所有文件都可以被访问
        // 如果要添加的话参照我的格式进行添加
        registry.addResourceHandler("/image/**").addResourceLocations("classpath:/image/");
        registry.addResourceHandler("/video/**").addResourceLocations("classpath:/video/");
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        // 跨域处理这里我已经处理好了不需要改动
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3006", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 配置自定义的JSON序列化/反序列化规则用的，这里一般不需要改动
        log.info("扩展消息转换器...");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
