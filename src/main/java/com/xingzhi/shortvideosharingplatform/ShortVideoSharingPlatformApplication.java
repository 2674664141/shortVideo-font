package com.xingzhi.shortvideosharingplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ServletComponentScan
@EnableCaching
@MapperScan("com.xingzhi.shortvideosharingplatform.mapper")
public class ShortVideoSharingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortVideoSharingPlatformApplication.class, args);
    }

}
