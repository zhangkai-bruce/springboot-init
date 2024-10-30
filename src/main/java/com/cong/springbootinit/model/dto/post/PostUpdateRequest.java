package com.cong.springbootinit.model.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Data
public class PostUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 标签列表
     */
    private List<String> tags;
}
