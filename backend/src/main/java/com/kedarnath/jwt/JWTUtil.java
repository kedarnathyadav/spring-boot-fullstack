package com.kedarnath.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JWTUtil {

    private static final String SECRET_KEY =
            "king_123456898_king_123456898king_123456898king_123456898king_123456898king_123456898";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        String token = Jwts
                .builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .issuer("http://kedarnath.com")
                .expiration(Date.from(now.plus(15, ChronoUnit.DAYS)))
                .claims(claims) // Keep claims map
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
        return token;
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        // Use parserBuilder instead of the deprecated parser()
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Signing key is used to verify the JWT signature
                .build()  // Build the parser
                .parseClaimsJws(token)  // Parse the JWT and retrieve claims
                .getBody();  // Return the Claims object
    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }


    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt).getExpiration().before(today);
    }
}