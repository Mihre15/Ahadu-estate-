package com.Ahadu_backend.app.core.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.listing.service.ListingService;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final ListingService listingService;
    private final BuyerRepository buyerRepository;
    private final AgentRepository agentRepository;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String q,
            @RequestParam(required = false) String propertyType,
            Model model) {
        boolean hasSearch = (q != null && !q.isBlank())
                || (propertyType != null && !propertyType.isBlank() && !"Property Type".equalsIgnoreCase(propertyType));

        model.addAttribute("listings", hasSearch
                ? listingService.searchListingEntities(q, propertyType)
                : listingService.getAllListingEntities());
        model.addAttribute("searchQuery", q);
        model.addAttribute("selectedPropertyType", propertyType);
        model.addAttribute("searchActive", hasSearch);
        return "index";
    }

    @GetMapping("/listings")
    public String listings(Model model) {
        model.addAttribute("listings", listingService.getAllListingEntities());
        return "listings";
    }

    @GetMapping("/listings/{id}")
    public String listingDetails(@PathVariable Long id, Model model) {
        model.addAttribute("listing", listingService.getListingEntityById(id));
        return "listingDetails";
    }

    @PostMapping("/listings/{id}/request")
    public String requestListing(@PathVariable Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        Listing listing = listingService.getListingEntityById(id);
        String email = authentication.getName();
        String role = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("");

        if (Role.BUYER.name().equals(role)) {
            Buyer buyer = buyerRepository.findByUserEmail(email)
                    .orElseThrow(() -> new RuntimeException("Buyer profile not found"));
            buyer.getRequestedListings().add(listing);
            buyerRepository.save(buyer);
            redirectAttributes.addFlashAttribute("success", "Listing added to your buying requests.");
            return "redirect:/buyer/me";
        }

        if (Role.AGENT.name().equals(role)) {
            Agent agent = agentRepository.findByUserEmail(email)
                    .orElseThrow(() -> new RuntimeException("Agent profile not found"));
            if (listing.getAgent() != null && listing.getAgent().getId().equals(agent.getId())) {
                redirectAttributes.addFlashAttribute("error", "You already own this listing.");
                return "redirect:/listings/" + id;
            }
            agent.getRequestedListings().add(listing);
            agentRepository.save(agent);
            redirectAttributes.addFlashAttribute("success", "Listing added to your requested properties.");
            return "redirect:/agent/dashboard";
        }

        redirectAttributes.addFlashAttribute("error", "Please log in as a buyer or agent to request a listing.");
        return "redirect:/login";
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
