package com.parser.server.controllers;

import com.parser.core.client.dto.ClientDto;
import com.parser.core.client.service.ClientService;
import com.parser.core.config.permission.annotation.ManualPermissionControl;
import com.parser.core.exceptions.ForbiddenException;
import com.parser.server.config.client.ClientTokenContextHolder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api")
public class AuthControllerRest {

    ClientService clientService;

    @ManualPermissionControl
    @GetMapping("/auth/current")
    public ClientDto current() {
        return ClientTokenContextHolder.currentOptional().map(
                context -> {
                    return ClientDto.fromEntity(context.getPrincipal());
                }
        ).orElseThrow(ForbiddenException::new);
    }
}
