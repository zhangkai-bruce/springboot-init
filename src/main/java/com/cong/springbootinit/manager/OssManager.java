package com.cong.springbootinit.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import com.cong.springbootinit.config.OSSConfig;
import com.cong.springbootinit.constant.CommonConstant;
import com.cong.springbootinit.constant.FileConstant;
import com.cong.springbootinit.utils.NetUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.cong.springbootinit.constant.CommonConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OssManager {

    private final String endpoint;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String bucketName;

    @Autowired
    public OssManager(OSSConfig ossConfig) {
        this.endpoint = ossConfig.getEndpoint();
        this.accessKeyId = ossConfig.getAccessKeyId();
        this.accessKeySecret = ossConfig.getAccessKeySecret();
        this.bucketName = ossConfig.getBucketName();
    }

    /**
     * 上传文件到OSS
     */
    public String uploadToOss(String fileName, MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            PutObjectResult putObjectResult = ossClient.putObject(bucketName, fileName, inputStream);
            String url = CommonConstant.HTTPS + bucketName + "." + endpoint + "/" + fileName;
            log.info("上传至oss成功！请求id：{}，响应路径：{}", putObjectResult.getRequestId(), url);
            ossClient.shutdown();
            return url;
        } catch (IOException e) {
            log.error("上传图片异常，{}", e.getMessage());
            return "上传失败！";
        }
    }

    /**
     * 上传文件到本地
     */
    public String uploadToLocal(String fileName, MultipartFile multipartFile) {
        try {
            // 确保目录存在
            File directory = FileUtil.mkdir(FileConstant.FILE_PATH);
            // 目标文件路径
            File destFile = new File(directory, fileName);
            try (InputStream inputStream = multipartFile.getInputStream()) {
                FileUtil.writeFromStream(inputStream, destFile);
            }
            // http://localhost:8101/api/opt/image/user_avatar/9E0D9F55AB8C42AE9D06E0858E85D427.png
            String url = HTTP + NetUtils.getLocalIpAddress() + ":" + PORT + PREFIX + FileConstant.FILE_PATH + fileName;
            log.info("上传成功，图片地址：{}", url);
            return url;
        } catch (IOException e) {
            log.error("上传文件异常: {}", e.getMessage());
            return "上传失败！";
        }
    }

    /**
     * 删除本地文件
     *
     * @param fileName fileName
     * @return return
     */
    public String deleteFileByLocal(String fileName) {
        try {
            String path = StrUtil.subAfter(fileName, "/api", true);
            File file = new File(path);
            if (!file.exists()) {
                log.error("文件不存在: {}", path);
                return "文件不存在！";
            }
            boolean isDeleted = FileUtil.del(file);
            if (isDeleted) {
                log.info("文件删除成功: {}", path);
                return "文件删除成功！";
            }
            return "删除文件失败！";
        } catch (Exception e) {
            log.error("删除文件异常: {}", e.getMessage());
            return "删除文件失败！";
        }
    }

    /**
     * 删除文件
     *
     * @param fileName fileName
     */
    public void deleteFileByOss(String fileName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(bucketName, fileName);
            log.info("删除成功！图片：{}", fileName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
