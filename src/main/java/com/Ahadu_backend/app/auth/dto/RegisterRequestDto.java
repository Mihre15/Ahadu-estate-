package com.Ahadu_backend.app.auth.dto;

import com.Ahadu_backend.app.auth.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    private String email;
    private String password;
    private Role role;
    private String name;
    private String phone;
}
