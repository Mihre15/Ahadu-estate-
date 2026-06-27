package com.Ahadu_backend.app.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Ahadu_backend.app.auth.dto.AuthResponseDto;
import com.Ahadu_backend.app.auth.dto.LoginRequestDto;
import com.Ahadu_backend.app.auth.dto.RegisterRequestDto;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.repository.UserRepository;
import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final AgentRepository agentRepository;
    private final BuyerRepository buyerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponseDto register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setEmail(dto.getEmail());
        user.setPassword(
                passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());

        User savedUser = userRepository.save(user);

        // Create specialized profile based on role
        if (com.Ahadu_backend.app.auth.model.Role.AGENT.equals(dto.getRole())) {
            Agent agent = new Agent();
            agent.setUser(savedUser);
            agent.setName(dto.getName());
            agent.setPhone(dto.getPhone());
            agentRepository.save(agent);
        } else if (com.Ahadu_backend.app.auth.model.Role.BUYER.equals(dto.getRole())) {
            Buyer buyer = new Buyer();
            buyer.setUser(savedUser);
            buyer.setName(dto.getName());
            buyer.setPhone(dto.getPhone());
            buyerRepository.save(buyer);
        }

        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDto(
                token,
                savedUser.getEmail(),
                savedUser.getRole());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(
                dto.getPassword(),
                user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDto(
                token,
                user.getEmail(),
                user.getRole());
    }
}
