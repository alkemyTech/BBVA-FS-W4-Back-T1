package com.magicdogs.alkywall.servicies;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTService {

    private static final String SECRET_KEY = "secret";
    private static final String ISSUER = "Alkywall";

    public String createToken(String userId, int minutesValid) {
        var algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withIssuer(ISSUER)
                .withClaim("userId", userId)
                .withExpiresAt(new Date(System.currentTimeMillis() + minutesValid * 60 * 1000))
                .sign(algorithm);
    }

    public DecodedJWT verifyToken(String token) throws JWTVerificationException {
        var algorithm = Algorithm.HMAC256(SECRET_KEY);
        var verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();
        return verifier.verify(token);
    }

    public String extractUserId(String token) {
        var jwt = JWT.decode(token);
        return jwt.getClaim("userId").asString();
    }
}