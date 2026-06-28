package com.Ahadu_backend.app.auth.service;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.dto.AuthResponseDto;
import com.Ahadu_backend.app.auth.dto.LoginRequestDto;
import com.Ahadu_backend.app.auth.dto.RegisterRequestDto;
import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, agentRepository, buyerRepository, passwordEncoder, jwtService);
    }

    @Test
    void registerCreatesBuyerProfileForBuyerRole() {
        RegisterRequestDto dto = registerDto(Role.BUYER);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        AuthResponseDto response = authService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<Buyer> buyerCaptor = ArgumentCaptor.forClass(Buyer.class);
        verify(userRepository).save(userCaptor.capture());
        verify(buyerRepository).save(buyerCaptor.capture());
        verify(agentRepository, never()).save(any(Agent.class));

        User savedUser = userCaptor.getValue();
        Buyer savedBuyer = buyerCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("sam@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.BUYER);
        assertThat(savedBuyer.getUser()).isSameAs(savedUser);
        assertThat(savedBuyer.getName()).isEqualTo("Sam Buyer");
        assertThat(savedBuyer.getPhone()).isEqualTo("0911000000");
        assertThat(response.getMessage()).isEqualTo("token");
        assertThat(response.getEmail()).isEqualTo("sam@example.com");
        assertThat(response.getRole()).isEqualTo(Role.BUYER);
    }

    @Test
    void registerCreatesAgentProfileForAgentRole() {
        RegisterRequestDto dto = registerDto(Role.AGENT);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn("token");

        authService.register(dto);

        ArgumentCaptor<Agent> agentCaptor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(agentCaptor.capture());
        verify(buyerRepository, never()).save(any(Buyer.class));
        assertThat(agentCaptor.getValue().getName()).isEqualTo("Sam Buyer");
        assertThat(agentCaptor.getValue().getPhone()).isEqualTo("0911000000");
        assertThat(agentCaptor.getValue().getUser().getRole()).isEqualTo(Role.AGENT);
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequestDto dto = registerDto(Role.BUYER);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
        verify(agentRepository, never()).save(any(Agent.class));
        verify(buyerRepository, never()).save(any(Buyer.class));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        User user = user("agent@example.com", "encoded", Role.AGENT);
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("agent@example.com");
        dto.setPassword("plain");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "encoded")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("login-token");

        AuthResponseDto response = authService.login(dto);

        assertThat(response.getMessage()).isEqualTo("login-token");
        assertThat(response.getEmail()).isEqualTo("agent@example.com");
        assertThat(response.getRole()).isEqualTo(Role.AGENT);
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = user("buyer@example.com", "encoded", Role.BUYER);
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("buyer@example.com");
        dto.setPassword("wrong");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        verify(jwtService, never()).generateToken(any(User.class));
    }

    private RegisterRequestDto registerDto(Role role) {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("sam@example.com");
        dto.setPassword("secret");
        dto.setRole(role);
        dto.setName("Sam Buyer");
        dto.setPhone("0911000000");
        return dto;
    }

    private User user(String email, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
