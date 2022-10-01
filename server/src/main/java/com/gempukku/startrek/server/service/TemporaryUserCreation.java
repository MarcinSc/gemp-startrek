package com.gempukku.startrek.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class TemporaryUserCreation {
    @Autowired
    private UserService userService;

    @PostConstruct
    public void createTestUsers() {
        try {
            userService.registerUser("test1", "test1@gmail.com", "testtest");
            userService.registerUser("test2", "test2@gmail.com", "testtest");
            System.out.println("Users test1 and test2 registered");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
