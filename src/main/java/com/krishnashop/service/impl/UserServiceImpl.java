package com.krishnashop.service.impl;

import com.krishnashop.dto.UserRegistrationDto;
import com.krishnashop.entity.Cart;
import com.krishnashop.entity.Role;
import com.krishnashop.entity.User;
import com.krishnashop.repository.CartRepository;
import com.krishnashop.repository.UserRepository;
import com.krishnashop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("An account with email " + registrationDto.getEmail() + " already exists.");
        }

        User user = new User();
        user.setFullName(registrationDto.getFullName());
        user.setEmail(registrationDto.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhone(registrationDto.getPhone());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Initialize persistent cart for the user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);
        savedUser.setCart(cart);

        return savedUser;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getAllCustomers() {
        return userRepository.findByRole(Role.ROLE_USER);
    }

    @Override
    public long countCustomers() {
        return userRepository.countByRole(Role.ROLE_USER);
    }
}
