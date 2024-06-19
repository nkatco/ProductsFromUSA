package com.example.productsfromusa.services;

import com.example.productsfromusa.DTo.LoginAdminDto;
import com.example.productsfromusa.DTo.RegisterAdminDto;
import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.repositories.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            AdminRepository adminRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin signup(RegisterAdminDto input) {
        logger.info("Attempting to sign up a new admin with full name: {}", input.getFullName());

        Admin user = new Admin();
        user.setFullName(input.getFullName());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        Admin savedUser = adminRepository.save(user);
        logger.info("Admin signed up successfully with full name: {}", savedUser.getFullName());

        return savedUser;
    }

    public Admin authenticate(LoginAdminDto input) {
        logger.info("Attempting to authenticate admin with full name: {}", input.getFullName());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getFullName(),
                        input.getPassword()
                )
        );

        Admin admin = adminRepository.findByFullName(input.getFullName())
                .orElseThrow(() -> {
                    logger.error("Authentication failed for admin with full name: {}", input.getFullName());
                    return new RuntimeException("Admin not found");
                });

        logger.info("Admin authenticated successfully with full name: {}", admin.getFullName());
        return admin;
    }
}