package com.cong.springbootinit.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.cong.springbootinit.common.ErrorCode;
import com.cong.springbootinit.constant.FileConstant;
import com.cong.springbootinit.exception.BusinessException;
import com.cong.springbootinit.manager.MinioManager;
import com.cong.springbootinit.manager.OssManager;
import com.cong.springbootinit.model.enums.FileUploadBizEnum;
import com.cong.springbootinit.service.FileService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final OssManager ossManager;
    private final MinioManager minioManager;

    /**
     * 上传文件
     * 1、阿里云oss
     * 2、上传至本地，通过虚拟资源映射展示
     * 3、腾讯云cos
     */
    public String uploadFile(MultipartFile multipartFile, String biz) {
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型错误，应在" + FileUploadBizEnum.getValues() + "范围内！");
        }
        if (multipartFile == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        // 文件目录：根据业务划分
        String originalFilename = multipartFile.getOriginalFilename();
        String filename = IdUtil.simpleUUID().toUpperCase(Locale.ROOT) + originalFilename.substring(originalFilename.lastIndexOf("."));
        // oss上传路径要求前面不能带 /
        String finalFileName = String.format("%s/%s", fileUploadBizEnum.getValue(), filename);
        return minioManager.putObject(multipartFile, finalFileName);
    }

    @Override
    public String deleteFile(String fileName) {
        return ossManager.deleteFileByLocal(fileName);
    }

    /**
     * 校验文件格式、大小
     */
    @ApiOperation(value = "校验文件")
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long oneM = 1024 * 1024 * 10;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > oneM) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 10M");
            }
            if (!FileConstant.FileType.FILE_TYPE.contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "暂不支持上传" + fileSuffix + "类型文件！");
            }
        }
    }
}
