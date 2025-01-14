package com.cong.springbootinit.constant;

/**
 * @Classname RedisKey
 * @Date 2024/11/29 0029 下午 12:07
 * @Created By bruce.zhang
 */
public interface RedisKey {
    /**
     * 验证码
     */
    String CODE = "code";

    /**
     * 防重提交 redis key
     */
    String REPEAT_SUBMIT_KEY = "repeat_submit::";

    /**
     * 连接符
     */
    String JOIN = ":";
}
