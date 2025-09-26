package com.example.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTUtil {
    private final String SECRET = "my-super-secret-key-with-special-characters@!#";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final long EXPIRATION_TIME = 1000*60*60; //1 hour

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((System.currentTimeMillis() + EXPIRATION_TIME)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserName(String jwt) {
       Claims body = getClaims(jwt);
      return body.getSubject();
//      return getClaims(jwt).getSubject();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public boolean validateToken(String userName, UserDetails userDetails,String token) {

    return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
        //TODO CHECK IF userName IS SAME AS userDetails
        //TODO CHECK IF TOKEN IS NOT EXPIRED
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
