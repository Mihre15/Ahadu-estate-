package com.Ahadu_backend.app.auth.service;

import com.Ahadu_backend.app.auth.dto.AuthResponseDto;
import com.Ahadu_backend.app.auth.dto.LoginRequestDto;
import com.Ahadu_backend.app.auth.dto.RegisterRequestDto;

public interface IAuthService {

    AuthResponseDto register(RegisterRequestDto dto);

    AuthResponseDto login(LoginRequestDto dto);
}