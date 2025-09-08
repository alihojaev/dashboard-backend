package com.parser.core.auth.permission.service;

import com.parser.core.auth.permission.dto.PermissionDto;

import java.util.List;

public interface PermissionService {
    List<PermissionDto> listAllAsModel();
}
