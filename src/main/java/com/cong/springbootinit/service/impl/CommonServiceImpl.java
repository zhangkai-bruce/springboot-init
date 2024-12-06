package com.cong.springbootinit.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.cong.springbootinit.common.ErrorCode;
import com.cong.springbootinit.constant.FileConstant;
import com.cong.springbootinit.constant.RedisKey;
import com.cong.springbootinit.exception.BusinessException;
import com.cong.springbootinit.service.CommonService;
import com.cong.springbootinit.utils.NetUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.img.ImgUtil.toBufferedImage;
import static com.cong.springbootinit.constant.CommonConstant.*;

/**
 * @Classname CommonServiceImpl
 * @Date 2024/12/6 0006 下午 13:54
 * @Created By bruce.zhang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final StringRedisTemplate stringRedisTemplate;
    private final Tesseract tesseract;

    @Override
    public String getCircleCaptcha() {
        // 定义图形验证码的长、宽、验证码位数、干扰圈圈数量
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(150, 50, 4, 30);
        // 设置背景颜色
        circleCaptcha.setBackground(new Color(249, 251, 220));
        // 生成四位验证码
        String code = RandomUtil.randomNumbers(4);
        stringRedisTemplate.opsForValue().set(RedisKey.CODE, code, 3, TimeUnit.MINUTES);
        // 生成验证码图片
        Image image = circleCaptcha.createImage(code);
        return getVerifyCodeUrl(image, code);
    }

    @Override
    public String ocrParsing(MultipartFile multipartFile) {
        try {
            InputStream sbs = new ByteArrayInputStream(multipartFile.getBytes());
            BufferedImage bufferedImage = ImageIO.read(sbs);
            // 对图片进行文字识别
            String result = tesseract.doOCR(bufferedImage);
            log.info("识别结果 {}", result);
            return result;
        } catch (IOException | TesseractException e) {
            log.error("识别错误：{}", e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * codeUrl
     *
     * @param image image
     * @return return
     */
    private String getVerifyCodeUrl(Image image, String code) {
        try {
            // 将 Image 转换为 BufferedImage
            BufferedImage bufferedImage = toBufferedImage(image);
            Files.createDirectories(Paths.get(FileConstant.CODE_PATH)); // 确保目录存在
            String fileName = code + ".jpeg";
            File outputFile = new File(FileConstant.CODE_PATH + fileName);
            ImageIO.write(bufferedImage, "jpeg", outputFile);
            clearFile(code);
            // 示例：http://localhost:8101/api/opt/code/9E0D9F55AB8C42AE9D06E0858E85D427.png
            String url = HTTP + NetUtils.getLocalIpAddress() + ":" + PORT + PREFIX + FileConstant.CODE_PATH + fileName;
            log.info("验证码图片地址：{}", url);
            return url;
        } catch (IOException e) {
            log.error("生成验证码失败：{}", e.getMessage());
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成验证码失败");
        }
    }


    /**
     * 删除多余文件
     *
     * @param code code
     */
    private void clearFile(String code) {
        // 取出 /opt/code目录下的所有图片文件
        java.util.List<File> files = FileUtil.loopFiles(FileConstant.CODE_PATH);
        List<File> fileList = files.stream()
                .filter(file -> !StringUtils.equals(FileUtil.mainName(file), code))
                .collect(Collectors.toList());
        fileList.forEach(FileUtil::del);
    }
}
