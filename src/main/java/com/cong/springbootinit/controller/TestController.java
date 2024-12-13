package com.cong.springbootinit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/test1")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public int dignifiedTest() throws Exception {
        int code = 0;
        System.out.println("dignifiedTest被调用,时间：" + LocalTime.now());
        if (code == 0) {
            throw new Exception("情况不对头！");
        }
        System.out.println("dignifiedTest被调用,情况对头了！");
        return 200;
    }

    @Recover
    public int recover(Exception e) {
        System.out.println("回调方法执行！！！！");
        //记日志到数据库 或者调用其余的方法
        return 400;
    }


}
