package com.cong.springbootinit.model.dto.user;

import lombok.Data;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest {
    /**
     * 用户名
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 验证码
     */
    private String code;
}
