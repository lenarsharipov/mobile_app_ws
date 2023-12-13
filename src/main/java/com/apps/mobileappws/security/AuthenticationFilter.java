package com.apps.mobileappws.security;

import com.apps.mobileappws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response
    ) throws AuthenticationException {
        try {
            UserLoginRequestModel credentials =
                    new ObjectMapper().readValue(
                            request.getInputStream(),
                            UserLoginRequestModel.class);

            return getAuthenticationManager().authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    credentials.getEmail(),
                                    credentials.getPassword(),
                                    Collections.emptyList()
                            )
                    );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        byte[] secretKeyBytes = Base64.getEncoder()
                .encode(SecurityConstants.TOKEN_SECRET.getBytes());
        SecretKey secretKey =
                new SecretKeySpec(
                        secretKeyBytes,
                        SignatureAlgorithm.HS512.getJcaName()
                );
        Instant now = Instant.now();
        String userName = ((User) authResult.getPrincipal()).getUsername();
        String token = Jwts.builder() // Access token
                .setSubject(userName)
                .setExpiration(Date.from(now.plusMillis(SecurityConstants.EXPIRATION_TIME)))
                .setIssuedAt(Date.from(now))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        response.addHeader(
                SecurityConstants.HEADER_STRING,
                SecurityConstants.TOKEN_PREFIX + token
        );
    }
}
