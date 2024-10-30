package com.cong.springbootinit.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Data
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
}
