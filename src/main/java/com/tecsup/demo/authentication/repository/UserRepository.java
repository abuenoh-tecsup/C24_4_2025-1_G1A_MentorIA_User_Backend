package com.tecsup.demo.authentication.repository;

import com.tecsup.demo.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
