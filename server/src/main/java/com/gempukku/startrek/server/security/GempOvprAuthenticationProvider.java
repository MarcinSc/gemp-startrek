package com.gempukku.startrek.server.security;

import com.gempukku.startrek.server.service.UserService;
import com.gempukku.startrek.server.service.vo.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class GempOvprAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getPrincipal() == null ? "" : authentication.getPrincipal().toString();
        String password = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

        LoggedUser returnedUser = userService.loginUser(name, password);
        return new UsernamePasswordAuthenticationToken(
                returnedUser.getUsername(), null, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
