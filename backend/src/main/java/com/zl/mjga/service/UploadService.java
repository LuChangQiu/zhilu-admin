package com.zl.mjga.service;

import com.zl.mjga.config.minio.MinIoConfig;
import com.zl.mjga.exception.BusinessException;
import io.minio.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadService {

  private final MinioClient minioClient;
  private final MinIoConfig minIoConfig;

  public String uploadAvatarFile(MultipartFile multipartFile) throws Exception {
    String originalFilename = multipartFile.getOriginalFilename();
    if (StringUtils.isEmpty(originalFilename)) {
      throw new BusinessException("文件名不能为空");
    }
    String contentType = multipartFile.getContentType();
    String extension = "";
    if ("image/jpeg".equals(contentType)) {
      extension = ".jpg";
    } else if ("image/png".equals(contentType)) {
      extension = ".png";
    }
    String objectName =
        String.format(
            "/library/%d%s%s",
            Instant.now().toEpochMilli(),
            RandomStringUtils.insecure().nextAlphabetic(6),
            extension);
    if (multipartFile.isEmpty()) {
      throw new BusinessException("上传的文件不能为空");
    }
    long size = multipartFile.getSize();
    if (size > 200 * 1024) {
      throw new BusinessException("头像大小不能超过200KB");
    }
    BufferedImage img = ImageIO.read(multipartFile.getInputStream());
    if (img == null) {
      throw new BusinessException("非法的上传文件");
    }
    minioClient.putObject(
        PutObjectArgs.builder().bucket(minIoConfig.getDefaultBucket()).object(objectName).stream(
                multipartFile.getInputStream(), size, -1)
            .contentType(multipartFile.getContentType())
            .build());
    return objectName;
  }

  public String uploadLibraryDoc(MultipartFile multipartFile) throws Exception {
    String originalFilename = multipartFile.getOriginalFilename();
    if (StringUtils.isEmpty(originalFilename)) {
      throw new BusinessException("文件名不能为空");
    }
    String objectName = String.format("/library/%s", originalFilename);
    if (multipartFile.isEmpty()) {
      throw new BusinessException("上传的文件不能为空");
    }
    long size = multipartFile.getSize();
    if (size > 1024 * 1024) {
      throw new BusinessException("知识库文档大小不能超过1MB");
    }
    minioClient.putObject(
        PutObjectArgs.builder().bucket(minIoConfig.getDefaultBucket()).object(objectName).stream(
                multipartFile.getInputStream(), size, -1)
            .contentType(multipartFile.getContentType())
            .build());
    return objectName;
  }
}
