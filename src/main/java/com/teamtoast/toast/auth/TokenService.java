package com.teamtoast.toast.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.teamtoast.toast.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TokenService {

    @Autowired
    private UserService userService;

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

    public User verifyToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);
        return userService.getUser(jwt.getClaim("id").asLong());
    }

}
