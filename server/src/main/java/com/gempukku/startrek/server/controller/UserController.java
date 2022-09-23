package com.gempukku.startrek.server.controller;

import com.gempukku.startrek.common.UserValidation;
import com.gempukku.startrek.server.service.UserConflictException;
import com.gempukku.startrek.server.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/register")
    public ResponseEntity<JSONObject> register(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password) {
        try {
            userService.registerUser(username, email, password);
            JSONObject response = new JSONObject();

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserConflictException exp) {
            JSONObject response = new JSONObject();
            response.put("error", exp.getMessage());

            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (UserValidation.UserValidationException exp) {
            JSONObject response = new JSONObject();
            response.put("error", exp.getMessage());

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
