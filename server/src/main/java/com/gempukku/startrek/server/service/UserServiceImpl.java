package com.gempukku.startrek.server.service;

import com.gempukku.startrek.common.UserValidation;
import com.gempukku.startrek.server.service.vo.LoggedUser;
import com.gempukku.startrek.server.service.vo.User;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {
    private static final char[] saltChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int saltLength = 8;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void registerUser(String username, String email, String password) throws UserValidation.UserValidationException,
            UserConflictException {
        UserValidation.validateUser(username, email, password);

        User user = new User();
        user.setUsername(username);
        user.setUsernameLowerCase(username.toLowerCase());
        user.setEmail(email);
        String salt = getRandomSalt();
        user.setSalt(salt);
        HashCode hashCode = calculateHash(salt, password);
        user.setPasswordHash(hashCode.toString());
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exp) {
            throw new UserConflictException();
        }
    }

    private String getRandomSalt() {
        Random rnd = new Random();
        char[] result = new char[saltLength];
        for (int i = 0; i < result.length; i++) {
            result[i] = saltChars[rnd.nextInt(saltChars.length)];
        }
        return new String(result);
    }

    @Override
    public LoggedUser loginUser(String username, String password) throws BadCredentialsException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new BadCredentialsException("Invalid username or password");

        String salt = user.getSalt();
        HashCode hashCode = calculateHash(salt, password);
        if (!user.getPasswordHash().equals(hashCode.toString()))
            throw new BadCredentialsException("Invalid username or password");

        return new LoggedUser(username);
    }

    private HashCode calculateHash(String salt, String password) {
        String saltedPassword = salt + password;
        return Hashing.sha256().hashBytes(saltedPassword.getBytes(StandardCharsets.UTF_8));
    }
}
