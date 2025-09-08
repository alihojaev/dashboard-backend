package com.parser.core.auth.permission.service;

import com.parser.core.auth.permission.dto.PermissionDto;
import com.parser.core.auth.permission.entity.Permission;
import com.parser.core.auth.permission.jdbc.PermissionDao;
import com.parser.core.auth.role.enums.PermissionType;
import com.parser.core.auth.screen.service.ScreenServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {

    ScreenServiceImpl screenService;
    PermissionDao dao;

    PermissionServiceImpl(
            ScreenServiceImpl screenService,
            PermissionDao dao) {
        this.screenService = screenService;
        this.dao = dao;
    }

    @Override
    public List<PermissionDto> listAllAsModel() {
        return dao.listAll();
    }

    public void initPermissions() {
        var screens = screenService.initScreens();

        var permissions = listAllAsModel().stream().map(p -> new Permission(
                p.getId(),
                p.getName(),
                p.getName().getDescription(),
                screens.get(p.getName().getScreenType()),
                new Timestamp(System.currentTimeMillis())
        )).collect(Collectors.toList());

        var permissionSet = permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        for (var p : PermissionType.values()) {
            if (!permissionSet.contains(p)) {
                permissions.add(
                        new Permission(
                                null,
                                p,
                                p.getDescription(),
                                screens.get(p.getScreenType()),
                                new Timestamp(System.currentTimeMillis())
                        )
                );
            }
        }

        if (dao.saveAll(permissions).size() != permissions.size()) {
            throw new RuntimeException();
        }
    }
}
