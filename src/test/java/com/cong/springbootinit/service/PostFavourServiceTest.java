package com.cong.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.springbootinit.model.entity.Post;
import com.cong.springbootinit.model.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子收藏服务测试
 * <p>
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@SpringBootTest
class PostFavourServiceTest {

    private static final User loginUser = new User();
    @Resource
    private PostFavourService postFavourService;

    @BeforeAll
    static void setUp() {
        loginUser.setId(1L);
    }

    @Test
    void doPostFavour() {
        int i = postFavourService.doPostFavour(1L, loginUser);
        Assertions.assertTrue(i >= 0);
    }

    @Test
    void listFavourPostByPage() {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.eq("id", 1L);
        postFavourService.listFavourPostByPage(Page.of(0, 1), postQueryWrapper, loginUser.getId());
    }
}
