package com.example.productsfromusa.configs;

import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.repositories.AdminRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Configuration
public class ApplicationConfiguration {
    private final AdminRepository adminRepository;

    public ApplicationConfiguration(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            Optional<Admin> adminOptional = adminRepository.findByFullName(username);
            if (adminOptional.isPresent()) {
                return adminOptional.get();
            } else {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        };
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
