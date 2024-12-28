package com.cong.springbootinit.config;

import lombok.Data;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.request.AuthGithubRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname GithubProperties
 * @Date 2024/12/18 0018 下午 16:03
 * @Created By bruce.zhang
 */
@Configuration
@Data
public class GithubConfig {
    @Value("${github.client-id}")
    private String clientId;
    @Value("${github.client-secret}")
    private String clientSecret;
    @Value("${github.redirect-uri}")
    private String redirectUri;

    public AuthRequest getAuthRequest() {
        return new AuthGithubRequest(
                AuthConfig.builder()
                        .clientId(clientId)
                        .clientSecret(clientSecret)
                        .redirectUri(redirectUri)
                        .build()
        );
    }
}
