package com.example.productsfromusa.services.data;

import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.repositories.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository userRepository) {
        this.adminRepository = userRepository;
    }

    public List<Admin> allUsers() {
        List<Admin> users = new ArrayList<>();

        adminRepository.findAll().forEach(users::add);

        return users;
    }
}
