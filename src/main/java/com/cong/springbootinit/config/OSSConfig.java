package com.cong.springbootinit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OSSConfig {
    /**
     * 节点
     */
    private String endpoint;
    /**
     * keyId
     */
    private String accessKeyId;
    /**
     * 密钥
     */
    private String accessKeySecret;
    /**
     * 桶名
     */
    private String bucketName;
}