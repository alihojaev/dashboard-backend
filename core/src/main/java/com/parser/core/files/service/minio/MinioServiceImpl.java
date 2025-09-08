package com.parser.core.files.service.minio;

import com.parser.core.files.enums.MinioBucket;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MinioServiceImpl implements MinioService {

    MinioClient minioClient;

    @Override
    public void uploadFile(String fileName, MinioBucket bucket, MultipartFile file) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket.name()).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket.name()).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket.name())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    @Override
    public InputStream downloadFile(String fileName, MinioBucket bucket) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket.name())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла: " + e.getMessage());
        }
    }
}