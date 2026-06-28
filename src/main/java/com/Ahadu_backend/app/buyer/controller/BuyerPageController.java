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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BuyerPageController {

    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;

    @GetMapping("/buyer/me")
    public String buyerProfile(Authentication authentication, Model model) {
        String email = authentication.getName();
        Buyer buyer = getOrCreateBuyer(email);

        model.addAttribute("buyer", buyer);
        model.addAttribute("userEmail", email);
        model.addAttribute("requestedListings", buyer.getRequestedListings());

        return "buyerProfile";
    }

    @PostMapping("/buyer/profile")
    public String updateBuyerProfile(Authentication authentication,
                                     @ModelAttribute Buyer form,
                                     RedirectAttributes redirectAttributes) {
        Buyer buyer = getOrCreateBuyer(authentication.getName());
        buyer.setName(form.getName());
        buyer.setPhone(form.getPhone());
        buyer.setPreferredCity(form.getPreferredCity());
        buyer.setBudgetMin(form.getBudgetMin());
        buyer.setBudgetMax(form.getBudgetMax());
        buyerRepository.save(buyer);
        redirectAttributes.addFlashAttribute("success", "Buyer profile updated.");
        return "redirect:/buyer/me";
    }

    private Buyer getOrCreateBuyer(String email) {
        return buyerRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Buyer newBuyer = new Buyer();
                    newBuyer.setUser(user);
                    return buyerRepository.save(newBuyer);
                });
    }
}
