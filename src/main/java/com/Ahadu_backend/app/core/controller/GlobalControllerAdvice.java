package com.Ahadu_backend.app.core.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final AgentRepository agentRepository;
    private final BuyerRepository buyerRepository;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // Initial default values to prevent Thymeleaf errors
        model.addAttribute("isLoggedIn", false);
        model.addAttribute("userEmail", null);
        model.addAttribute("userRole", null);
        model.addAttribute("agent", null);
        model.addAttribute("buyer", null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (isLoggedIn) {
            model.addAttribute("isLoggedIn", true);
            String email = authentication.getName();
            String role = authentication.getAuthorities()
                    .stream()
                    .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                    .findFirst()
                    .orElse("");

            model.addAttribute("userEmail", email);
            model.addAttribute("userRole", role);

            if ("AGENT".equals(role)) {
                agentRepository.findByUserEmail(email).ifPresent(agent -> model.addAttribute("agent", agent));
            } else if ("BUYER".equals(role)) {
                buyerRepository.findByUserEmail(email).ifPresent(buyer -> model.addAttribute("buyer", buyer));
            }
        }
    }
}
