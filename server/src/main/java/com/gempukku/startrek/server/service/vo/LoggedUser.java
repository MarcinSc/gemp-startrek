package com.gempukku.startrek.server.service.vo;

public class LoggedUser {
    private String username;

    public LoggedUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
