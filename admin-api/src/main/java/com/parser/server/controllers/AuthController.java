package com.parser.server.controllers;


import com.parser.core.auth.login.dto.LoginDto;
import com.parser.core.auth.login.service.LoginService;
import com.parser.core.auth.permission.service.PermissionServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    LoginService loginService;
    PermissionServiceImpl permissionService;

    @PostMapping(value = "/api/public/auth/login")
    @ApiImplicitParam(name = "Authorization", required = false)
    public Map<String, String> login(@RequestBody LoginDto model) {
        return Collections.singletonMap(
                "token",
                loginService.login(
                        model,
                        true
                )
        );
    }
}
