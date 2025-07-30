package com.dhd.gymmanagement.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    private static final String SECRET = "12345678901234567890123456789012";
    private static final long EXPIRATION_MS = 86400000;

    public static String generateToken(Long userId, String email, String role) throws Exception {
        JWSSigner signer = new MACSigner(SECRET);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("role", role)
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .issueTime(new Date())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public static Map<String, Object> validateTokenAndGetClaims(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET);

        if (signedJWT.verify(verifier)) {
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiration.after(new Date())) {
                JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
                Map<String, Object> result = new HashMap<>();
                result.put("userId", claims.getLongClaim("userId"));
                result.put("email", claims.getStringClaim("email"));
                result.put("role", claims.getStringClaim("role"));
                return result;
            }
        }
        return null;
    }
} 