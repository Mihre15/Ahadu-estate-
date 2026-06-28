package com.Ahadu_backend.app.agent.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.listing.service.ListingService;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentPageControllerTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private ListingService listingService;

    private AgentPageController controller;

    @BeforeEach
    void setUp() {
        controller = new AgentPageController(agentRepository, userRepository, buyerRepository, listingService);
    }

    @Test
    void agentDashboardCreatesProfileWhenMissingAndAddsListings() {
        User user = user("agent@example.com");
        Agent savedAgent = agent(user, 2L);
        Listing listing = new Listing();
        savedAgent.getListings().add(listing);
        when(agentRepository.findByUserEmail("agent@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("agent@example.com")).thenReturn(Optional.of(user));
        when(agentRepository.save(any(Agent.class))).thenReturn(savedAgent);
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.agentDashboard(new TestingAuthenticationToken("agent@example.com", "password"), model);

        assertThat(view).isEqualTo("agentDashboard");
        assertThat(model.get("agent")).isSameAs(savedAgent);
        assertThat(model.get("listings")).isEqualTo(List.of(listing));
        verify(agentRepository).save(any(Agent.class));
    }

    @Test
    void agentListingDetailsAddsRequestingBuyersForOwnedListing() {
        User user = user("agent@example.com");
        Agent agent = agent(user, 2L);
        Listing listing = new Listing();
        listing.setId(12L);
        listing.setAgent(agent);
        Buyer buyer = new Buyer();
        buyer.setName("Buyer One");
        when(listingService.getOwnedListing(12L, "agent@example.com")).thenReturn(listing);
        when(buyerRepository.findDistinctByRequestedListingsId(12L)).thenReturn(List.of(buyer));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.agentListingDetails(12L, new TestingAuthenticationToken("agent@example.com", "password"), model);

        assertThat(view).isEqualTo("agentListingDetails");
        assertThat(model.get("listing")).isSameAs(listing);
        assertThat(model.get("requestingBuyers")).isEqualTo(List.of(buyer));
    }

    @Test
    void createListingDelegatesToListingServiceAndRedirectsToDashboard() {
        ListingRequestDto dto = new ListingRequestDto();
        MockMultipartFile image = new MockMultipartFile("imageFile", "home.jpg", "image/jpeg", "image".getBytes());
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.createListing(
                new TestingAuthenticationToken("agent@example.com", "password"),
                dto,
                image,
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/agent/dashboard");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Listing published successfully.");
        verify(listingService).createListingForAgent(dto, image, "agent@example.com");
    }

    @Test
    void updateAgentProfileSavesCurrentAgentFields() {
        User user = user("agent@example.com");
        Agent currentAgent = agent(user, 2L);
        Agent form = new Agent();
        form.setName("Updated Agent");
        form.setPhone("0911222333");
        form.setAgencyName("Ahadu Realty");
        form.setLicenseNumber("LIC-123");
        when(agentRepository.findByUserEmail("agent@example.com")).thenReturn(Optional.of(currentAgent));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.updateAgentProfile(
                new TestingAuthenticationToken("agent@example.com", "password"),
                form,
                redirectAttributes
        );

        ArgumentCaptor<Agent> agentCaptor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(agentCaptor.capture());
        assertThat(view).isEqualTo("redirect:/agent/dashboard");
        assertThat(agentCaptor.getValue().getName()).isEqualTo("Updated Agent");
        assertThat(agentCaptor.getValue().getAgencyName()).isEqualTo("Ahadu Realty");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Agent profile updated.");
    }

    @Test
    void deleteListingDelegatesOwnershipAwareDeleteAndRedirects() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.deleteListing(
                15L,
                new TestingAuthenticationToken("agent@example.com", "password"),
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/agent/dashboard");
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Listing deleted successfully.");
        verify(listingService).deleteListingForAgent(15L, "agent@example.com");
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setRole(Role.AGENT);
        return user;
    }

    private Agent agent(User user, Long id) {
        Agent agent = new Agent();
        agent.setId(id);
        agent.setUser(user);
        agent.setName("Agent Name");
        return agent;
    }
}

