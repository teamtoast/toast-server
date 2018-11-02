package com.teamtoast.toast.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.teamtoast.toast.Application;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TokenService {

    @Value("${token-secret}")
    private String tokenSecret;
    private Algorithm algorithm;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(tokenSecret);
    }

    public String newToken(long id, User.AccountType type) {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("type", type.toString().toLowerCase())
                .sign(algorithm);
    }

}
