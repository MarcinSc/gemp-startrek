package com.gempukku.startrek.common;

import com.artemis.BaseSystem;

public class AuthenticationHolderSystem extends BaseSystem {
    private String username;
    private String authenticationToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        this.authenticationToken = authenticationToken;
    }

    @Override
    protected void processSystem() {

    }
}
