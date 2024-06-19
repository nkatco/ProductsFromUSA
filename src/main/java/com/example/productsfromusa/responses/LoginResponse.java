package com.example.productsfromusa.responses;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginResponse {
    private String token;

    private long expiresIn;

    public String getToken() {
        return token;
    }
}
