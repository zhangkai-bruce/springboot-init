package com.cong.springbootinit.controller;

import com.cong.springbootinit.common.BaseResponse;
import com.cong.springbootinit.common.ResultUtils;
import com.cong.springbootinit.manager.VerifyCodeManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname CommonController
 * @Date 2024/11/29 0029 上午 9:28
 * @Created By bruce.zhang
 * 通用接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
    private final VerifyCodeManager verifyCodeManager;

    @RequestMapping("/verifyCode")
    public BaseResponse<String> getCircleCaptcha() {
        return ResultUtils.success(verifyCodeManager.getCircleCaptcha());
    }
}
