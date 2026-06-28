package com.Ahadu_backend.app.buyer.controller;

import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyerPageControllerTest {

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private UserRepository userRepository;

    private BuyerPageController controller;

    @BeforeEach
    void setUp() {
        controller = new BuyerPageController(buyerRepository, userRepository);
    }

    @Test
    void buyerProfileCreatesBuyerWhenMissingAndShowsRequestedListings() {
        User user = user("buyer@example.com");
        Buyer savedBuyer = new Buyer();
        savedBuyer.setUser(user);
        Listing requestedListing = new Listing();
        savedBuyer.getRequestedListings().add(requestedListing);
        when(buyerRepository.findByUserEmail("buyer@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(buyerRepository.save(any(Buyer.class))).thenReturn(savedBuyer);
        ExtendedModelMap model = new ExtendedModelMap();

        String view = controller.buyerProfile(new TestingAuthenticationToken("buyer@example.com", "password"), model);

        assertThat(view).isEqualTo("buyerProfile");
        assertThat(model.get("buyer")).isSameAs(savedBuyer);
        assertThat(model.get("userEmail")).isEqualTo("buyer@example.com");
        assertThat(model.get("requestedListings")).isEqualTo(Set.of(requestedListing));
        verify(buyerRepository).save(any(Buyer.class));
    }

    @Test
    void updateBuyerProfileSavesCurrentBuyerFields() {
        Buyer buyer = new Buyer();
        buyer.setUser(user("buyer@example.com"));
        Buyer form = new Buyer();
        form.setName("Updated Buyer");
        form.setPhone("0911444555");
        form.setPreferredCity("Addis Ababa");
        form.setBudgetMin(100000.0);
        form.setBudgetMax(500000.0);
        when(buyerRepository.findByUserEmail("buyer@example.com")).thenReturn(Optional.of(buyer));
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = controller.updateBuyerProfile(
                new TestingAuthenticationToken("buyer@example.com", "password"),
                form,
                redirectAttributes
        );

        ArgumentCaptor<Buyer> buyerCaptor = ArgumentCaptor.forClass(Buyer.class);
        verify(buyerRepository).save(buyerCaptor.capture());
        assertThat(view).isEqualTo("redirect:/buyer/me");
        assertThat(buyerCaptor.getValue().getName()).isEqualTo("Updated Buyer");
        assertThat(buyerCaptor.getValue().getPreferredCity()).isEqualTo("Addis Ababa");
        assertThat(buyerCaptor.getValue().getBudgetMax()).isEqualTo(500000.0);
        assertThat(redirectAttributes.getFlashAttributes().get("success")).isEqualTo("Buyer profile updated.");
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setRole(Role.BUYER);
        return user;
    }
}
