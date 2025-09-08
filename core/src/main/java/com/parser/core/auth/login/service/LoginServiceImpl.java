package com.parser.core.auth.login.service;

import com.google.common.base.Strings;
import com.lambdaworks.crypto.SCryptUtil;
import com.parser.core.admin.entity.AdminEntity;
import com.parser.core.auth.core.jwt.TokenService;
import com.parser.core.auth.core.service.AuthService;
import com.parser.core.auth.login.dto.LoginDto;
import com.parser.core.exceptions.EmptyPasswordException;
import com.parser.core.exceptions.EmptyUsernameException;
import com.parser.core.exceptions.ForbiddenException;
import com.parser.core.exceptions.UserNotFoundException;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginServiceImpl implements LoginService {

    AuthService authService;
    TokenService jwt;

    public LoginServiceImpl(
            AuthService authService, TokenService jwt) {
        this.authService = authService;
        this.jwt = jwt;
    }

    @Override
    public AdminEntity userFromToken(String token) {
        String username = jwt.getUsername(token);

        var user = authService.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (user.getBlocked() || user.getRdt() != null) throw new UserNotFoundException();

        user.setRoles(user.getAuthRoles());
        return user;
    }

    @Override
    public String login(LoginDto model, Boolean checkActive) {
        authService.findByUsername(model.username()).orElseThrow(ForbiddenException::new);

        try {
            AdminEntity u = verify(model.username(), model.password(), checkActive);


            return jwt.sign(
                    u.getUsername(),
                    u.getPassword()
            );
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        }
    }

    private AdminEntity verify(String username, String password, Boolean checkActive) {
        if (Strings.isNullOrEmpty(username)) throw new EmptyUsernameException();

        if (Strings.isNullOrEmpty(password)) throw new EmptyPasswordException();

        Optional<AdminEntity> entity = authService.findByUsername(username);

        return entity.map(
                        auth -> {
                            if (!checkActive && (!auth.isEnabled() || auth.getRdt() != null)) {
                                throw new UserNotFoundException();
                            }

                            var passwordHash = auth.getPassword();
                            if (Strings.isNullOrEmpty(passwordHash)) {
                                throw new UserNotFoundException();
                            }

                            if (checkActive) {
                                checkPassword(password, passwordHash);
                            }

                            return auth;
                        }
                )
                .orElseThrow(UserNotFoundException::new);
    }

    private void checkPassword(String password, String passwordHash) {
        if (!SCryptUtil.check(password, passwordHash))
            throw new UserNotFoundException();
    }
}

