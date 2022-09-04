package com.gempukku.startrek.server.service;

import com.gempukku.startrek.common.UserValidation;
import com.gempukku.startrek.server.service.vo.LoggedUser;
import org.springframework.security.authentication.BadCredentialsException;

public interface UserService {
    void registerUser(String username, String email, String password) throws UserValidation.UserValidationException;
    LoggedUser loginUser(String username, String password) throws BadCredentialsException;
}
