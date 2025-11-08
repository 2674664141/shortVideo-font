package com.xingzhi.shortvideosharingplatform.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户基础信息表实体类
 */
@Data
@TableName("users")
public class User {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号是否已验证：0-未验证 1-已验证
     */
    private Integer phoneVerified;

    /**
     * 邮箱是否已验证：0-未验证 1-已验证
     */
    private Integer emailVerified;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 地区
     */
    private String region;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 账号状态：0-注销 1-正常 2-冻结 3-封禁
     */
    private Integer status;

    /**
     * 登录失败次数
     */
    private Integer loginFailedCount;

    /**
     * 账号锁定到期时间
     */
    private LocalDateTime lockedUntil;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 注册来源：1-手机号 2-邮箱 3-微信 4-QQ 5-微博
     */
    private Integer registerSource;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 性别枚举
     */
    public enum Gender {
        UNKNOWN(0, "未知"),
        MALE(1, "男"),
        FEMALE(2, "女");

        private final int code;
        private final String desc;

        Gender(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 账号状态枚举
     */
    public enum Status {
        DELETED(0, "注销"),
        NORMAL(1, "正常"),
        FROZEN(2, "冻结"),
        BANNED(3, "封禁");

        private final int code;
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 注册来源枚举
     */
    public enum RegisterSource {
        PHONE(1, "手机号"),
        EMAIL(2, "邮箱"),
        WECHAT(3, "微信"),
        QQ(4, "QQ"),
        WEIBO(5, "微博");

        private final int code;
        private final String desc;

        RegisterSource(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}