package com.cong.springbootinit.model.dto.user;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest {

    @NotNull(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须在3到20之间")
    private String userAccount;

    @NotNull
    @Size(min = 8, max = 20, message = "密码长度必须在8到20之间")
    private String userPassword;

    @NotNull
    @Size(min = 8, max = 20, message = "密码长度必须在8到20之间")
    private String checkPassword;
}
