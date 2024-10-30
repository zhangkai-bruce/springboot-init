package com.cong.springbootinit.model.dto.postthumb;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞请求
 * # @author <a href="https://github.com/zhangkai-bruce">bruce</a>
 */
@Data
public class PostThumbAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 帖子 id
     */
    private Long postId;
}
