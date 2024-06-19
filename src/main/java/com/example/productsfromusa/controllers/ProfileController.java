package com.example.productsfromusa.controllers;

import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.services.data.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/profile")
@RestController
public class ProfileController {
    private final AdminService adminService;

    public ProfileController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/me")
    public ResponseEntity<Admin> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Admin currentUser = (Admin) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }
}
