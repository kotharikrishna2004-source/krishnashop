package com.krishnashop.service;

import com.krishnashop.dto.UserRegistrationDto;
import com.krishnashop.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(UserRegistrationDto registrationDto);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    List<User> getAllCustomers();

    long countCustomers();
}
