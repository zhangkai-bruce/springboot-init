package com.cong.springbootinit.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 文件常量
 */
public interface FileConstant {

    /**
     * 图片路径
     */
    String FILE_PATH = "/opt/image/";

    /**
     * 验证码路径
     */
    String CODE_PATH = "/opt/code/";


    /**
     * 文件类型限制
     */
    class FileType {
        public static final List<String> FILE_TYPE = Arrays.asList("jpeg", "jpg", "svg", "png", "webp");
    }
}
