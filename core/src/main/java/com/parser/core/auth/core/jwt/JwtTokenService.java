package com.parser.core.auth.core.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class JwtTokenService implements TokenService {

    private static final String USERNAME = "username";
    private static final String TYPE = "type";

    int userExpirationSec;

    JwtTokenService(@Value("${jwt.user-expiration-sec:3600000}") final int userExpirationSec) {
        this.userExpirationSec = userExpirationSec;
    }

    @Override
    public String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USERNAME).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    @Override
    public boolean verify(final String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(USERNAME, username)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("verify(): exc: {}, msg: {}", e.getClass().getCanonicalName(), e.getMessage());
            return false;
        }
    }


    @Override
    public String sign(String username, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date date = new Date(System.currentTimeMillis() + SECONDS.toMillis(this.userExpirationSec));

        return JWT.create()
                .withClaim(USERNAME, username)
                .withExpiresAt(date)
                .sign(algorithm);
    }
}
