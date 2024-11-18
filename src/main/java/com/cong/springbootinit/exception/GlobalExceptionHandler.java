package com.cong.springbootinit.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import com.cong.springbootinit.common.BaseResponse;
import com.cong.springbootinit.common.ErrorCode;
import com.cong.springbootinit.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * # @author <a href="https://github.com/zhangkai-bruce">bruce</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public BaseResponse<?> handleNotLoginException(NotLoginException e) {
        // 定义提示信息
        String message;
        switch (e.getType()) {
            case NotLoginException.NOT_TOKEN:
                message = "未提供Token";
                break;
            case NotLoginException.INVALID_TOKEN:
                message = "未提供有效的Token";
                break;
            case NotLoginException.TOKEN_TIMEOUT:
                message = "登录信息已过期，请重新登录";
                break;
            case NotLoginException.BE_REPLACED:
                message = "您的账户已在另一台设备上登录，如非本人操作，请立即修改密码";
                break;
            case NotLoginException.KICK_OUT:
                message = "已被系统强制下线";
                break;
            default:
                message = "当前会话未登录";
        }
        // 返回错误信息
        return ResultUtils.error(401, message);
    }


    @ExceptionHandler
    public BaseResponse<?> handlerNotRoleException(NotRoleException e) {
        return ResultUtils.error(403, "无此角色：" + e.getRole());
    }

    @ExceptionHandler
    public BaseResponse<?> handlerNotPermissionException(NotPermissionException e) {
        return ResultUtils.error(403, "无此权限：" + e.getCode());
    }

//    @ExceptionHandler
//    public BaseResponse<?> handlerDisableLoginException(DisableLoginException e) {
//        return ResultUtils.error(401, "账户被封禁：" + e.getDisableTime() + "秒后解封");
//    }

    @ExceptionHandler
    public BaseResponse<?> handlerNotSafeException(NotSafeException e) {
        return ResultUtils.error(401, "二级认证异常：" + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
