package com.Ahadu_backend.app.buyer.controller;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class BuyerPageController {

    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;

    @GetMapping("/buyer/me")
    public String buyerProfile(Authentication authentication, Model model) {
        String email = authentication.getName();

        Buyer buyer = buyerRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    // Create new buyer profile if it doesn't exist
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Buyer newBuyer = new Buyer();
                    newBuyer.setUser(user);
                    return buyerRepository.save(newBuyer);
                });

        model.addAttribute("buyer", buyer);
        model.addAttribute("userEmail", email);

        return "buyerProfile";
    }
}