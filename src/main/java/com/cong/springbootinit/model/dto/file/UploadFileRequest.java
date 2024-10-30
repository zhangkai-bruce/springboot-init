package com.cong.springbootinit.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 * # @author <a href="https://github.com/lhccong">程序员聪</a>
 */
@Data
public class UploadFileRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 业务
     */
    private String biz;
}
