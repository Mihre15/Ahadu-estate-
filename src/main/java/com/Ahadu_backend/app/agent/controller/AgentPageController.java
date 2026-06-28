package com.Ahadu_backend.app.agent.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.listing.service.ListingService;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AgentPageController {

    private final AgentRepository agentRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final ListingService listingService;

    @GetMapping("/agent/dashboard")
    public String agentDashboard(Authentication authentication, Model model) {
        Agent agent = getOrCreateAgent(authentication.getName());

        model.addAttribute("agent", agent);
        model.addAttribute("listings", agent.getListings());
        model.addAttribute("requestedListings", agent.getRequestedListings());

        return "agentDashboard";
    }

    @PostMapping("/agent/profile")
    public String updateAgentProfile(Authentication authentication,
                                     @ModelAttribute Agent form,
                                     RedirectAttributes redirectAttributes) {
        Agent agent = getOrCreateAgent(authentication.getName());
        agent.setName(form.getName());
        agent.setPhone(form.getPhone());
        agent.setAgencyName(form.getAgencyName());
        agent.setLicenseNumber(form.getLicenseNumber());
        agentRepository.save(agent);
        redirectAttributes.addFlashAttribute("success", "Agent profile updated.");
        return "redirect:/agent/dashboard";
    }

    @GetMapping("/agent/listings/{id}")
    public String agentListingDetails(@PathVariable Long id, Authentication authentication, Model model) {
        Listing listing = listingService.getOwnedListing(id, authentication.getName());

        model.addAttribute("listing", listing);
        model.addAttribute("requestingBuyers", buyerRepository.findDistinctByRequestedListingsId(id));

        return "agentListingDetails";
    }

    @GetMapping("/agent/listings/new")
    public String addListingForm(Model model) {
        model.addAttribute("listing", new ListingRequestDto());
        model.addAttribute("formTitle", "Publish a property");
        model.addAttribute("formAction", "/agent/listings");
        model.addAttribute("submitLabel", "Publish listing");
        model.addAttribute("isEdit", false);
        return "listingForm";
    }

    @PostMapping("/agent/listings")
    public String createListing(Authentication authentication,
                                @ModelAttribute ListingRequestDto listing,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        listingService.createListingForAgent(listing, imageFile, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Listing published successfully.");
        return "redirect:/agent/dashboard";
    }

    @GetMapping("/agent/listings/{id}/edit")
    public String editListingForm(@PathVariable Long id, Authentication authentication, Model model) {
        Listing listing = listingService.getOwnedListing(id, authentication.getName());
        ListingRequestDto dto = new ListingRequestDto();
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setAddress(listing.getAddress());
        dto.setCity(listing.getCity());
        dto.setPrice(listing.getPrice());
        dto.setBedrooms(listing.getBedrooms());
        dto.setBathrooms(listing.getBathrooms());
        dto.setArea(listing.getArea());
        dto.setImage(listing.getImage());
        dto.setPropertyType(listing.getPropertyType());
        dto.setListingStatus(listing.getListingStatus());

        model.addAttribute("listing", dto);
        model.addAttribute("listingId", id);
        model.addAttribute("currentImage", listing.getImage());
        model.addAttribute("formTitle", "Update property listing");
        model.addAttribute("formAction", "/agent/listings/" + id);
        model.addAttribute("submitLabel", "Save changes");
        model.addAttribute("isEdit", true);
        return "listingForm";
    }

    @PostMapping("/agent/listings/{id}")
    public String updateListing(@PathVariable Long id,
                                Authentication authentication,
                                @ModelAttribute ListingRequestDto listing,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        listingService.updateListingForAgent(id, listing, imageFile, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Listing updated successfully.");
        return "redirect:/agent/listings/" + id;
    }

    @PostMapping("/agent/listings/{id}/delete")
    public String deleteListing(@PathVariable Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        listingService.deleteListingForAgent(id, authentication.getName());
        redirectAttributes.addFlashAttribute("success", "Listing deleted successfully.");
        return "redirect:/agent/dashboard";
    }

    private Agent getOrCreateAgent(String email) {
        return agentRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Agent newAgent = new Agent();
                    newAgent.setUser(user);
                    return agentRepository.save(newAgent);
                });
    }
}
