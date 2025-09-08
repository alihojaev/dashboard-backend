package com.parser.core.files.service;

import com.parser.core.files.dto.FileDto;
import com.parser.core.files.entity.FileEntity;
import com.parser.core.files.enums.MinioBucket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FileService {

    byte[] download(MinioBucket minioBucket, UUID fileId);

    Page<FileDto> searchPageable(Specification<FileEntity> specification, Pageable pageable);

    List<FileDto> search(Specification<FileEntity> specification);

    FileDto getById(UUID id);

    void delete(UUID id);

    FileEntity uploadFile(String originalFilename, MinioBucket minioBucket, MultipartFile file);
}
