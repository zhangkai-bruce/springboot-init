package com.cong.springbootinit.service;

import com.cong.springbootinit.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子点赞服务测试
 * <p>
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@SpringBootTest
class PostThumbServiceTest {

    private static final User loginUser = new User();
    @Resource
    private PostThumbService postThumbService;

    @BeforeAll
    static void setUp() {
        loginUser.setId(1L);
    }

    @Test
    void doPostThumb() {
        int i = postThumbService.doPostThumb(1L, loginUser);
        Assertions.assertTrue(i >= 0);
    }
}
