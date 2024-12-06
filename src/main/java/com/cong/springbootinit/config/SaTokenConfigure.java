package com.cong.springbootinit.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
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
            "/favicon.ico",
            "/swagger-resources",
            "/v2/api-docs",
            "/file/**"
    );

    // 接口路径集合
    List<String> apiPaths = Arrays.asList(
            "/opt/**", // 本地图片映射
            "/user/login", // 登录
            "/user/register", // 注册
            "/file/**", // 测试上传时候，生产关闭
            "/common/**"

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
}
