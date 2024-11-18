package com.cong.springbootinit.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 静态资源路径集合
    List<String> staticResourcePaths = Arrays.asList(
            "/",
            "/index",
            "/**/*.img",
            "/**/*.jpg",
            "/**/*.svg",
            "/**/*.gif",
            "/**/*.png",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/i18n/**",
            "/**/*.ttf",
            "/doc.html",
            "/swagger-resources"
    );

    // 接口路径集合
    List<String> apiPaths = Arrays.asList(
            "/user/login",
            "/user/register",
            "/user/getlogin"

    );

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(staticResourcePaths)
                .excludePathPatterns(apiPaths);
    }

    /**
     * 注册 [Sa-Token全局过滤器]
     */
//    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定 拦截路由 与 放行路由
                .addInclude("/**").addExclude("/auth/doLogin").addExclude("/auth/isLogin").addExclude("/auth/logout")
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    System.out.println("---------- 进入Sa-Token全局认证 -----------");
                    // 登录认证 -- 拦截所有路由，并排除/auth/doLogin 用于开放登录
                    SaRouter.match("/**", "/auth/doLogin", () -> StpUtil.checkLogin());
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    System.out.println("---------- 进入Sa-Token异常处理 -----------");
                    return SaResult.error(e.getMessage());
                })
                // 前置函数：在每次认证函数之前执行
                .setBeforeAuth(obj -> {
                    // ---------- 设置跨域响应头 ----------
                    SaHolder.getResponse()
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*");
                });
    }
}
