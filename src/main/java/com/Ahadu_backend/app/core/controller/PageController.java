package com.Ahadu_backend.app.core.controller;


import com.Ahadu_backend.app.listing.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ListingService listingService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("listings", listingService.getAllListings());
        return "index";
    }

    @GetMapping("/listings")
    public String listings(Model model) {
        model.addAttribute("listings", listingService.getAllListings());
        return "listings";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
}
