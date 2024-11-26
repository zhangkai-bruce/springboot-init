package com.cong.springbootinit.service;

import com.cong.springbootinit.model.dto.file.UploadFileRequest;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传文件
     *
     * @param multipartFile     文件
     * @param uploadFileRequest 类型
     * @return 文件访问地址
     */
    String uploadFile(MultipartFile multipartFile, UploadFileRequest uploadFileRequest);


    /**
     * 删除文件
     *
     * @param fileName fileName
     * @return return
     */
    String deleteFile(String fileName);
}
