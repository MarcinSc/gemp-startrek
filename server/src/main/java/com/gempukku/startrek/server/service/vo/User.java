package com.gempukku.startrek.server.service.vo;

import javax.persistence.*;

@Entity(name = "gemp_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;
    @Column(unique = true)
    private String usernameLowerCase;
    private String salt;
    @Column(unique = true)
    private String email;
    private String passwordHash;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameLowerCase() {
        return usernameLowerCase;
    }

    public void setUsernameLowerCase(String usernameLowerCase) {
        this.usernameLowerCase = usernameLowerCase;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}