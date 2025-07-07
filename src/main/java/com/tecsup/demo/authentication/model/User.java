package com.tecsup.demo.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_superuser", nullable = false)
    private Boolean isSuperuser = false;

    @Column(name = "username", nullable = false, unique = true, length = 150)
    private String username;

    @Column(name = "is_staff", nullable = false)
    private Boolean isStaff = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "date_joined", nullable = false)
    private LocalDateTime dateJoined;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 30)
    private String lastName;

    @Column(name = "profile_picture_url", length = 200)
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private UserRole role;

    // Relaciones
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Professor professor;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Student student;

    // Implementación de UserDetails
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name().toUpperCase()));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isActive;
    }

    public enum UserRole {
        student, professor, admin
    }
}