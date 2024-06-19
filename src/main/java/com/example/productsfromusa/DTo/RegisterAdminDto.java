package com.example.productsfromusa.DTo;

import lombok.Data;

@Data
public class RegisterAdminDto {

    private String password;
    private String fullName;
    private String securityToken;
}
