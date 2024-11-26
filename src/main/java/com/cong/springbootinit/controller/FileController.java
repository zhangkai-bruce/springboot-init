package com.cong.springbootinit.controller;

import com.cong.springbootinit.common.BaseResponse;
import com.cong.springbootinit.common.ResultUtils;
import com.cong.springbootinit.model.dto.file.UploadFileRequest;
import com.cong.springbootinit.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 */
@RestController
@RequestMapping("/file")
@Api(tags = "文件操作")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile, UploadFileRequest uploadFileRequest) {
        return ResultUtils.success(fileService.uploadFile(multipartFile, uploadFileRequest));
    }

    /**
     * 上传文件
     */
    @PostMapping("/delete")
    @ApiOperation(value = "文件删除")
    public BaseResponse<String> deleteFile(String fileName) {
        return ResultUtils.success(fileService.deleteFile(fileName));
    }
}
