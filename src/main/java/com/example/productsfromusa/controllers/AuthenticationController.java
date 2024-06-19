package com.example.productsfromusa.controllers;

import com.example.productsfromusa.DTo.LoginAdminDto;
import com.example.productsfromusa.DTo.RegisterAdminDto;
import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.services.AuthenticationService;
import com.example.productsfromusa.services.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    @Value("${security.token}")
    private String securityToken;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginUserDto", new LoginAdminDto());
        return "login";
    }

    @GetMapping("/signup")
    public String showSignupPage(Model model) {
        model.addAttribute("registerUserDto", new RegisterAdminDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute("registerUserDto") RegisterAdminDto registerUserDto, Model model) {
        if (!securityToken.equals(registerUserDto.getSecurityToken())) {
            model.addAttribute("error", "Invalid security token");
            return "signup";
        }
        authenticationService.signup(registerUserDto);
        return "redirect:/auth/login";
    }

    @PostMapping("/login")
    public String authenticate(@ModelAttribute("loginUserDto") LoginAdminDto loginUserDto, Model model) {
        Admin authenticatedUser = authenticationService.authenticate(loginUserDto);

        if (authenticatedUser != null) {
            String jwtToken = jwtService.generateToken(authenticatedUser);
            model.addAttribute("token", jwtToken);
            return "redirect:/admin";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }
}
