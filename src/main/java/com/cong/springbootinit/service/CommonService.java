package com.cong.springbootinit.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname CommonService
 * @Date 2024/12/6 0006 下午 13:54
 * @Created By bruce.zhang
 */
public interface CommonService {
    /**
     * 获取图片验证码
     */
    String getCircleCaptcha();

    /**
     * ocr识别
     */
    String ocrParsing(MultipartFile multipartFile);
}
