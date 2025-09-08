package com.parser.core.auth.login.service;

import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.login.dto.LoginDto;


public interface LoginService {

    AdminEntity userFromToken(String token);

    String login(LoginDto model, Boolean checkActive);

}
