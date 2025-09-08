package com.parser.core.files.service;

import com.parser.core.exceptions.BadRequestException;
import com.parser.core.files.dto.FileDto;
import com.parser.core.files.entity.FileEntity;
import com.parser.core.files.enums.MinioBucket;
import com.parser.core.files.mapper.FileMapper;
import com.parser.core.files.repo.FileRepo;
import com.parser.core.files.service.minio.MinioService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.binary.Hex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileServiceImpl implements FileService {

    MinioService minioService;
    FileRepo repo;
    FileMapper fileMapper;

    @Override
    public FileEntity uploadFile(String originalFilename, MinioBucket minioBucket, MultipartFile file) {
        String fileName = getSHA256Checksum(file);
        String fileExtension = getFileExtension(originalFilename);
        String filePath = fileName + "." + fileExtension;

        var exist = repo.findByNameAndRdtIsNull(fileName);

        if (exist.isPresent()) {
            return exist.get();
        } else {
            minioService.uploadFile(filePath, minioBucket, file);

            FileEntity entity = new FileEntity(
                    null,
                    minioBucket,
                    fileExtension,
                    fileName,
                    filePath,
                    LocalDateTime.now(),
                    null
            );

            return repo.save(entity);
        }
    }

    @Override
    public byte[] download(MinioBucket minioBucket, UUID fileId) {
        var file = findById(fileId);
        try {
            InputStream stream = minioService.downloadFile(file.getPath(), minioBucket);
            return stream.readAllBytes();
        } catch (Exception e) {
            return null;
        }
    }

    private String getSHA256Checksum(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (InputStream inputStream = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hashBytes = digest.digest();
            return Hex.encodeHexString(hashBytes);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }


    @Override
    @Transactional
    public Page<FileDto> searchPageable(Specification<FileEntity> specification, Pageable pageable) {
        return repo.findAll(specification, pageable).map(fileMapper::toDto);
    }

    @Override
    @Transactional
    public List<FileDto> search(Specification<FileEntity> specification) {
        return fileMapper.toDtoList(repo.findAll(specification));
    }

    @Override
    @Transactional
    public FileDto getById(UUID id) {
        return fileMapper.toDto(findById(id));
    }

    private FileEntity findById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new BadRequestException("record not found"));
    }


    @Override
    public void delete(UUID id) {
        FileEntity entity = findById(id);
        entity.setRdt(LocalDateTime.now());
        repo.save(entity);
    }
}