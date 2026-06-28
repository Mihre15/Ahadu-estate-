package com.Ahadu_backend.app.core.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalControllerAdviceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private BuyerRepository buyerRepository;

    private GlobalControllerAdvice advice;

    @BeforeEach
    void setUp() {
        advice = new GlobalControllerAdvice(agentRepository, buyerRepository);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addGlobalAttributesUsesAgentNameAndProfileUrlWhenAgentIsLoggedIn() {
        Agent agent = new Agent();
        agent.setName("Amina Agent");
        when(agentRepository.findByUserEmail("amina@example.com")).thenReturn(Optional.of(agent));
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("amina@example.com", "password", "ROLE_AGENT")
        );
        ExtendedModelMap model = new ExtendedModelMap();

        advice.addGlobalAttributes(model);

        assertThat(model.get("isLoggedIn")).isEqualTo(true);
        assertThat(model.get("userEmail")).isEqualTo("amina@example.com");
        assertThat(model.get("userRole")).isEqualTo("AGENT");
        assertThat(model.get("userDisplayName")).isEqualTo("Amina Agent");
        assertThat(model.get("profileUrl")).isEqualTo("/agent/dashboard");
        assertThat(model.get("agent")).isSameAs(agent);
    }

    @Test
    void addGlobalAttributesUsesBuyerNameAndProfileUrlWhenBuyerIsLoggedIn() {
        Buyer buyer = new Buyer();
        buyer.setName("Bereket Buyer");
        when(buyerRepository.findByUserEmail("bereket@example.com")).thenReturn(Optional.of(buyer));
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("bereket@example.com", "password", "BUYER")
        );
        ExtendedModelMap model = new ExtendedModelMap();

        advice.addGlobalAttributes(model);

        assertThat(model.get("isLoggedIn")).isEqualTo(true);
        assertThat(model.get("userRole")).isEqualTo("BUYER");
        assertThat(model.get("userDisplayName")).isEqualTo("Bereket Buyer");
        assertThat(model.get("profileUrl")).isEqualTo("/buyer/me");
        assertThat(model.get("buyer")).isSameAs(buyer);
    }

    @Test
    void addGlobalAttributesFallsBackToEmailWhenProfileNameIsBlank() {
        Agent agent = new Agent();
        agent.setName(" ");
        when(agentRepository.findByUserEmail("agent@example.com")).thenReturn(Optional.of(agent));
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("agent@example.com", "password", "AGENT")
        );
        ExtendedModelMap model = new ExtendedModelMap();

        advice.addGlobalAttributes(model);

        assertThat(model.get("userDisplayName")).isEqualTo("agent@example.com");
    }

    @Test
    void addGlobalAttributesSetsLoggedOutDefaultsWithoutAuthentication() {
        ExtendedModelMap model = new ExtendedModelMap();

        advice.addGlobalAttributes(model);

        assertThat(model.get("isLoggedIn")).isEqualTo(false);
        assertThat(model.get("userEmail")).isNull();
        assertThat(model.get("profileUrl")).isNull();
    }

    private User user(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setRole(role);
        return user;
    }
}
