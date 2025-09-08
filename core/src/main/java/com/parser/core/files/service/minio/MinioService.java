package com.parser.core.files.service.minio;

import com.parser.core.files.enums.MinioBucket;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {


    void uploadFile(String fileName, MinioBucket bucket, MultipartFile file);

    InputStream downloadFile(String fileName, MinioBucket bucket);
}
