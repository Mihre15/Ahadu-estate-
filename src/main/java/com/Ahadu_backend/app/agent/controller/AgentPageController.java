package com.Ahadu_backend.app.agent.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AgentPageController {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;

    @GetMapping("/agent/dashboard")
    public String agentDashboard(Authentication authentication, Model model) {
        String email = authentication.getName();

        Agent agent = agentRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    // Create new agent profile if it doesn't exist
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Agent newAgent = new Agent();
                    newAgent.setUser(user);
                    return agentRepository.save(newAgent);
                });

        model.addAttribute("agent", agent);
        model.addAttribute("listings", agent.getListings());

        return "agentDashboard";
    }

    @GetMapping("/agent/listings/new")
    public String addListingForm() {
        return "listingForm";
    }
}