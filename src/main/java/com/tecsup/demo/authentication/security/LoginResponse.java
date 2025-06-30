package com.tecsup.demo.authentication.security;

import lombok.Data;

@Data
public class LoginResponse {
    private Long id; // ← nuevo campo
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;

    public LoginResponse(Long id, String token, String username, String email, String role) {
        this.id = id;
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
