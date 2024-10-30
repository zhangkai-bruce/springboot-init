package com.cong.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cong.springbootinit.model.dto.post.PostQueryRequest;
import com.cong.springbootinit.model.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子服务测试
 * <p>
 * # @author <a href="https://github.com/zhangkai-bruce">bruce</a>
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

    @Test
    void searchFromEs() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setUserId(1L);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        Assertions.assertNotNull(postPage);
    }

}
