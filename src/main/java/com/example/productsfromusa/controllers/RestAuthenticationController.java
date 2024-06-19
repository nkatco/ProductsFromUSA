package com.example.productsfromusa.controllers;

import com.example.productsfromusa.DTo.LoginAdminDto;
import com.example.productsfromusa.models.Admin;
import com.example.productsfromusa.services.AuthenticationService;
import com.example.productsfromusa.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/rest")
public class RestAuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public RestAuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginAdminDto loginUserDto, HttpServletResponse response) {
        Admin authenticatedUser = authenticationService.authenticate(loginUserDto);

        if (authenticatedUser != null) {
            String jwtToken = jwtService.generateToken(authenticatedUser);

            Cookie cookie = new Cookie("jwtToken", jwtToken);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of("token", jwtToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        }
    }
}
