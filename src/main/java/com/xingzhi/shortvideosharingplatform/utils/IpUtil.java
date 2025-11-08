package com.xingzhi.shortvideosharingplatform.utils;

import javax.servlet.http.HttpServletRequest;

//获取IP地址工具类
public class IpUtil {
        public static String getClientIpAddress(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String xfor = request.getHeader("X-Forwarded-For");
        
        if (xfor != null && xfor.length() != 0 && !"unknown".equalsIgnoreCase(xfor)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = xfor.indexOf(",");
            if (index != -1) {
                return xfor.substring(0, index);
            } else {
                return xfor;
            }
        }
        
        xfor = xip;
        if (xfor != null && xfor.length() != 0 && !"unknown".equalsIgnoreCase(xfor)) {
            return xfor;
        }
        
        if (xfor == null || xfor.length() == 0 || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("Proxy-Client-IP");
        }
        
        if (xfor == null || xfor.length() == 0 || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (xfor == null || xfor.length() == 0 || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("HTTP_CLIENT_IP");
        }
        
        if (xfor == null || xfor.length() == 0 || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        
        if (xfor == null || xfor.length() == 0 || "unknown".equalsIgnoreCase(xfor)) {
            xfor = request.getRemoteAddr();
        }
        
        return xfor;
    }
}
