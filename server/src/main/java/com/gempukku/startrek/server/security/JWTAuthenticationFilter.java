package com.gempukku.startrek.server.security;

import com.auth0.jwt.JWT;
import org.json.simple.JSONObject;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.regex.Pattern;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.gempukku.startrek.server.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private Pattern supportedVersionPattern;

    public JWTAuthenticationFilter(Pattern supportedVersionPattern) {
        this.supportedVersionPattern = supportedVersionPattern;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String version = request.getParameter("version");
        if (version == null || !supportedVersionPattern.matcher(version).matches())
            throw new DisabledException("Invalid client version - please update");
        return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException {
        String loggedUser = (String) auth.getPrincipal();

        String token = JWT.create()
                .withSubject(loggedUser)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        res.addHeader(HEADER_STRING, token);

        res.getWriter().write(auth.getPrincipal().toString());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        JSONObject data = new JSONObject();
        data.put("message", failed.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getOutputStream().write(data.toJSONString().getBytes(StandardCharsets.UTF_8));
    }
}
