package com.springsecuritylatest.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class JwtUtils {

    private String jwtSigningKey = "secret";


    //retrieve username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    //retrieve expiration

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //
    public boolean hasClaim(String token, String claimName) {
        final Claims claim = extractAllClaims(token);
        return claim.get(claimName) != null;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

   //to claim all details from token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
               .setSigningKey(jwtSigningKey)
               .parseClaimsJws(token)
               .getBody();
    }

    //
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());

    }


    //Generate Token from
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims,userDetails);
    }


    //Generated Token
    public String generateToken(UserDetails userDetails, Map<String, Object> claims ) {
        return createToken(claims, userDetails);
    }

    //createToken
    public String createToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
                .signWith(SignatureAlgorithm.HS256,jwtSigningKey).compact();
    }

    //validate the Token
    public boolean isValidateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        }

}
