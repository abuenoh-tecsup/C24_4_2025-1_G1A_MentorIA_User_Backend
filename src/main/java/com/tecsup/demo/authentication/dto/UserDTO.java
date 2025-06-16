package com.tecsup.demo.authentication.dto;

import com.tecsup.demo.authentication.model.User.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private String password;
    private String username;
    private Boolean isStaff = false;
    private Boolean isActive = true;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private UserRole role;
}