package com.Ahadu_backend.app.auth.dto;

import com.Ahadu_backend.app.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDto {
    // private Long userId;
    private String message;

    private String email;

    private Role role;

}
