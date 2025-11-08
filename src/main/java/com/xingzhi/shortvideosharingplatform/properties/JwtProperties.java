package com.xingzhi.shortvideosharingplatform.properties;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
public class JwtProperties {
    /**
     * 管理端员工生成jwt令牌相关配置
     */
    @Value("${jwt.admin-secret-key}")
    private String adminSecretKey;
    @Value("${jwt.admin-ttl}")
    private long adminTtl;
    @Value("${jwt.admin-token-name}")
    private String adminTokenName;

    /**
     * 用户生成jwt令牌相关配置
     */
    @Value("${jwt.user-secret-key}")
    private String userSecretKey;
    @Value("${jwt.user-ttl}")
    private long userTtl;
    @Value("${jwt.user-token-name}")
    private String userTokenName;
}
