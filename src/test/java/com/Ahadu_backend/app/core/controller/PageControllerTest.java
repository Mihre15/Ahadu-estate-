package com.Ahadu_backend.app.core.controller;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.listing.service.ListingService;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageControllerTest {

    @Mock
    private ListingService listingService;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private AgentRepository agentRepository;

    private PageController pageController;

    @BeforeEach
    void setUp() {
        pageController = new PageController(listingService, buyerRepository, agentRepository);
    }

    @Test
    void homeAddsListingsAndReturnsIndexView() {
        Listing listing = new Listing();
        when(listingService.getAllListingEntities()).thenReturn(List.of(listing));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = pageController.home(null, null, model);

        assertThat(view).isEqualTo("index");
        assertThat(model.get("listings")).isEqualTo(List.of(listing));
    }

    @Test
    void homeUsesSearchWhenQueryOrPropertyTypeIsProvided() {
        Listing listing = new Listing();
        when(listingService.searchListingEntities("bole", "Apartment")).thenReturn(List.of(listing));
        ExtendedModelMap model = new ExtendedModelMap();

        String view = pageController.home("bole", "Apartment", model);

        assertThat(view).isEqualTo("index");
        assertThat(model.get("listings")).isEqualTo(List.of(listing));
        assertThat(model.get("searchQuery")).isEqualTo("bole");
        assertThat(model.get("selectedPropertyType")).isEqualTo("Apartment");
        assertThat(model.get("searchActive")).isEqualTo(true);
    }
    @Test
    void listingDetailsAddsSelectedListing() {
        Listing listing = new Listing();
        listing.setId(3L);
        when(listingService.getListingEntityById(3L)).thenReturn(listing);
        ExtendedModelMap model = new ExtendedModelMap();

        String view = pageController.listingDetails(3L, model);

        assertThat(view).isEqualTo("listingDetails");
        assertThat(model.get("listing")).isSameAs(listing);
    }

    @Test
    void buyerRequestListingAddsListingToBuyerRequests() {
        Listing listing = new Listing();
        listing.setId(9L);
        Buyer buyer = new Buyer();
        when(listingService.getListingEntityById(9L)).thenReturn(listing);
        when(buyerRepository.findByUserEmail("buyer@example.com")).thenReturn(Optional.of(buyer));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = pageController.requestListing(
                9L,
                new TestingAuthenticationToken("buyer@example.com", "password", "ROLE_BUYER"),
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/buyer/me");
        assertThat(buyer.getRequestedListings()).containsExactly(listing);
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Listing added to your buying requests.");
        verify(buyerRepository).save(buyer);
    }

    @Test
    void agentRequestListingRejectsOwnListing() {
        Agent owner = agent("agent@example.com", 4L);
        Listing listing = new Listing();
        listing.setId(9L);
        listing.setAgent(owner);
        when(listingService.getListingEntityById(9L)).thenReturn(listing);
        when(agentRepository.findByUserEmail("agent@example.com")).thenReturn(Optional.of(owner));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = pageController.requestListing(
                9L,
                new TestingAuthenticationToken("agent@example.com", "password", "ROLE_AGENT"),
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/listings/9");
        assertThat(redirectAttributes.getFlashAttributes().get("error")).isEqualTo("You already own this listing.");
        verify(agentRepository, never()).save(owner);
    }

    @Test
    void agentRequestListingAddsOtherAgentListingToRequestedProperties() {
        Agent requester = agent("requester@example.com", 5L);
        Listing listing = new Listing();
        listing.setId(9L);
        listing.setAgent(agent("owner@example.com", 4L));
        when(listingService.getListingEntityById(9L)).thenReturn(listing);
        when(agentRepository.findByUserEmail("requester@example.com")).thenReturn(Optional.of(requester));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = pageController.requestListing(
                9L,
                new TestingAuthenticationToken("requester@example.com", "password", "AGENT"),
                redirectAttributes
        );

        assertThat(view).isEqualTo("redirect:/agent/dashboard");
        assertThat(requester.getRequestedListings()).containsExactly(listing);
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Listing added to your requested properties.");
        verify(agentRepository).save(requester);
    }

    private Agent agent(String email, Long id) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setRole(Role.AGENT);
        Agent agent = new Agent();
        agent.setId(id);
        agent.setName("Agent " + id);
        agent.setUser(user);
        return agent;
    }
}



