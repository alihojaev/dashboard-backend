package com.parser.server.controllers;

import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.config.permission.annotation.HasPermission;
import com.parser.core.files.enums.MinioBucket;
import com.parser.core.files.service.FileService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
@HasPermission(PermissionType.DASHBOARD)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {

    FileService service;

    @PostMapping("/{bucket}/upload")
    public UUID uploadFile(@RequestParam("file") MultipartFile file, @PathVariable MinioBucket bucket) {
        return service.uploadFile(file.getOriginalFilename(), bucket, file).getId();
    }

    @ApiResponse(responseCode = "200",
            description = "Файл",
            content = @Content(
                    mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")
            )
    )
    @GetMapping(value = "/{bucket}/download/{fileName}")
    public byte[] downloadFile(@PathVariable UUID fileName, @PathVariable MinioBucket bucket) {
        return service.download(bucket, fileName);
    }
}
