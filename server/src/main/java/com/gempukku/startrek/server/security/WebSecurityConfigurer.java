package com.gempukku.startrek.server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Value("${gempukku.startrek.supported.version.pattern}")
    private Pattern supportedVersionPattern;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authManager = authenticationManager();
        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter(supportedVersionPattern);
        authenticationFilter.setAuthenticationManager(authManager);
        JWTAuthorizationFilter authorizationFilter = new JWTAuthorizationFilter(authManager);

        http
                .addFilter(authenticationFilter)
                .addFilter(authorizationFilter);

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login", "/register", "/download/**").permitAll()
                .anyRequest().authenticated();
    }
}
