package org.j2os.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
    Bahador, Amirsam
 */
@Component
@Slf4j
public class TokenProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.issuer}")
    private String issuer;
//    @Value("${jwt.access-token-expiration}")
//    private long accessTokenExpiration;
//    @Value("${jwt.refresh-token-expiration}")
//    private long refreshTokenExpiration;

    public String getAccessToken(String username){
        // Set the time of token expiration
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, 20);
        Date expirationDate = cal.getTime();

        String accessToken = JWT.create()
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secretKey));
        return accessToken;
    }

    public String getRefreshToken(String username){
        // Set the time of token expiration
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 1);
        Date refreshTokenExpirationDate = cal.getTime();

        // Generate the refresh token
        String refreshToken = JWT.create()
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withExpiresAt(refreshTokenExpirationDate)
                .sign(Algorithm.HMAC256(secretKey));
        return refreshToken;
    }

    public Map<String, String> getToken(String username) throws IllegalArgumentException, JWTCreationException {
        log.info("getToken");
        Map<String, String> tokens = new HashMap<>();

        String accessToken = getAccessToken(username);
        String refreshToken = getRefreshToken(username);

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String verifyToken(String token) throws JWTVerificationException {
        log.info("verifyToken");
        return JWT.require(Algorithm.HMAC256(secretKey))
                .withIssuer(issuer)
                .build().verify(token).getClaim("username").asString();
    }
}
