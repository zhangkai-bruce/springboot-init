package com.cong.springbootinit.controller;

import cn.hutool.core.util.IdUtil;
import com.cong.springbootinit.config.GithubConfig;
import com.cong.springbootinit.model.vo.TokenLoginUserVo;
import com.cong.springbootinit.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户接口
 */
@Api(tags = "第三方登录")
@Slf4j
@RestController
@RequestMapping("/third/login")
@RequiredArgsConstructor
public class ThirdPartyLoginController {

    private final UserService userService;
    private final GithubConfig githubConfig;

    /**
     * 跳转至登录页
     */
    @GetMapping("/getAuthorizationUrl")
    @ApiOperation(value = "跳转至登录页")
    public void renderAuth(HttpServletResponse response) throws IOException {
        AuthRequest authRequest = githubConfig.getAuthRequest();
        String state = IdUtil.simpleUUID();
        response.sendRedirect(authRequest.authorize(state));
    }

    /**
     * 进入回调获取登录信息
     */
    @GetMapping("/github/callback")
    @ApiOperation(value = "执行回调函数")
    public void userLoginByGithub(String code, String state, HttpServletResponse response) throws IOException {
        AuthCallback authCallback = new AuthCallback();
        authCallback.setCode(code);
        authCallback.setState(state);
        TokenLoginUserVo tokenLoginUserVo = userService.userLoginByGithub(authCallback);
        String url = "http://localhost:3333/#/auth-redirect?token=" + tokenLoginUserVo.getSaTokenInfo().getTokenValue();
        log.info("跳转至中转页面：url信息：{}", url);
        response.sendRedirect(url);
    }
}
