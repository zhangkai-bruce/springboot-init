package com.cong.springbootinit.controller;

import com.cong.springbootinit.common.BaseResponse;
import com.cong.springbootinit.common.ResultUtils;
import com.cong.springbootinit.model.dto.file.UploadFileRequest;
import com.cong.springbootinit.service.CommonService;
import com.cong.springbootinit.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Classname CommonController
 * @Date 2024/11/29 0029 上午 9:28
 * @Created By bruce.zhang
 * 通用接口
 */
@Api(tags = "通用接口")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
    private final CommonService commonService;
    private final FileService fileService;

    @ApiOperation(value = "获取验证码")
    @GetMapping("/verifyCode")
    public BaseResponse<String> getCircleCaptcha() {
        return ResultUtils.success(commonService.getCircleCaptcha());
    }

    /**
     * 上传文件
     */
    @GetMapping("/upload")
    @ApiOperation(value = "文件上传")
    public BaseResponse<String> uploadFile(@RequestParam(value = "file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest) {
        return ResultUtils.success(fileService.uploadFile(multipartFile, uploadFileRequest));
    }

    /**
     * 删除文件
     */
    @PostMapping("/delete")
    @ApiOperation(value = "文件删除")
    public BaseResponse<String> deleteFile(String fileName) {
        return ResultUtils.success(fileService.deleteFile(fileName));
    }


    @ApiOperation(value = "ocr图片解析识别")
    @PostMapping("/ocrParsing")
    public BaseResponse<String> ocrParsing(@Param("file") MultipartFile multipartFile) {
        return ResultUtils.success(commonService.ocrParsing(multipartFile));
    }
}
