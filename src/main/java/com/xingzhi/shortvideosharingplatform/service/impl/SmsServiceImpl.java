package com.xingzhi.shortvideosharingplatform.service.impl;

import com.xingzhi.shortvideosharingplatform.service.SmsService;
import com.xingzhi.shortvideosharingplatform.utils.SMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {
    
    @Autowired
    private SMSUtil smsUtil;
    
    // 存储验证码的内存缓存（生产环境建议使用Redis）
    private final ConcurrentHashMap<String, String> codeCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Override
    public boolean sendVerificationCode(String phone) {
        try {
            // 生成4位验证码
            String code = smsUtil.generateVerificationCode();
            
            // 发送短信
            boolean success = smsUtil.sendSMS(phone, code);
            
            if (success) {
                // 存储验证码，5分钟后自动删除
                codeCache.put(phone, code);
                scheduler.schedule(() -> codeCache.remove(phone), 5, TimeUnit.MINUTES);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String phone, String code) {
        String cachedCode = codeCache.get(phone);
        if (cachedCode != null && cachedCode.equals(code)) {
            // 验证成功后删除验证码
            codeCache.remove(phone);
            return true;
        }
        return false;
    }
}
