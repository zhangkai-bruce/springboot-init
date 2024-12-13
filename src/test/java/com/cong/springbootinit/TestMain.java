package com.cong.springbootinit;

import cn.hutool.http.HttpUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname TestMain
 * @Date 2024/12/13 0013 下午 14:02
 * @Created By bruce.zhang
 */
@SpringBootTest
public class TestMain {

    /**
     * 测试重试接口
     */
    @Test
    void test(){
        String result = HttpUtil.get("http://localhost:8101/api/test/test1");
        System.out.println(result);
    }
}
