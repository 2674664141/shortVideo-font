package com.xingzhi.shortvideosharingplatform.utils;

import java.util.HashMap;
import java.util.Random;
import org.springframework.stereotype.Component;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import lombok.extern.slf4j.Slf4j;

//短信验证码工具类
@Component
@Slf4j
public class SMSUtil {

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 是否发送成功
     */
    public boolean sendSMS(String phone, String code) {
        try {
            //生产环境请求地址：app.cloopen.com
            String serverIp = "app.cloopen.com";
            //请求端口
            String servePort = "8883";
            //主账号，登录云通讯网站后，可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
            String accountSId = "2c94811c9787a27f0197a5be67b70a3a";
            String accountToken = "6e45dad19abb4ee594e54be63ecd3b0a";
            //请使用管理控制台中已创建应用地APPID
            String appId = "2c94811c9787a27f0197a5c1547d0a47";
            
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(serverIp, servePort);
            sdk.setAccount(accountSId, accountToken);
            sdk.setAppId(appId);
            sdk.setBodyType(BodyType.Type_JSON);
            
            String to = phone;
            String templateId = "1";//免费开发测试使用的短信模板ID为1
            String expiry = "5"; // 5分钟有效期
            String[] datas = {code, expiry};
            
            //发送
            HashMap<String, Object> result = sdk.sendTemplateSMS(to, templateId, datas);
            
            //结果
            if("000000".equals(result.get("statusCode"))){
                log.info("短信验证码发送成功，手机号：{}，验证码：{}", phone, code);
                return true;
            } else {
                log.error("短信验证码发送失败，错误码：{}，错误信息：{}", result.get("statusCode"), result.get("statusMsg"));
                return false;
            }
        } catch (Exception e) {
            log.error("发送短信验证码异常：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成4位数字验证码（免费模板要求）
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 保持原有方法兼容性
    public String generateSMS(String phone) {
        String code = generateVerificationCode();
        boolean success = sendSMS(phone, code);
        return success ? code : null;
    }
}
