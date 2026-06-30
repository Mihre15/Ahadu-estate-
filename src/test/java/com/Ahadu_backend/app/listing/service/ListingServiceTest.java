package com.Ahadu_backend.app.listing.service;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.Role;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.core.service.FileStorageService;
import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.dto.ListingResponseDto;
import com.Ahadu_backend.app.listing.mapper.ListingMapper;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.listing.search.ListingSearchService;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.BuyerRepository;
import com.Ahadu_backend.app.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListingServiceTest {

    @Mock
    private ListingRepository listingRepository;

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ListingSearchService listingSearchService;

    @Mock
    private ListingMapper listingMapper;

    private ListingService listingService;

    @BeforeEach
    void setUp() {

        listingService = new ListingService(
                listingRepository,
                agentRepository,
                buyerRepository,
                new ListingMapper(),
                fileStorageService,
                listingSearchService);
    }

    @Test
    void createListingForAgentUsesLoggedInAgentUploadedImageAndDefaults() {
        Agent agent = agent("agent@example.com", 7L);
        ListingRequestDto dto = listingDto();
        dto.setPropertyType(null);
        dto.setListingStatus(" ");
        MockMultipartFile image = new MockMultipartFile("imageFile", "home.png", "image/png", "data".getBytes());
        when(agentRepository.findByUserEmail("agent@example.com")).thenReturn(Optional.of(agent));
        when(fileStorageService.storeImage(image)).thenReturn("/uploads/home.png");
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Listing listing = listingService.createListingForAgent(dto, image, "agent@example.com");

        assertThat(listing.getAgent()).isSameAs(agent);
        assertThat(listing.getTitle()).isEqualTo("Sunny apartment");
        assertThat(listing.getImage()).isEqualTo("/uploads/home.png");
        assertThat(listing.getPropertyType()).isEqualTo("Residential");
        assertThat(listing.getListingStatus()).isEqualTo("For Sale");
        verify(listingRepository).save(listing);
        verify(listingSearchService).indexListing(listing);
    }

    @Test
    void createListingForAgentFailsWhenAgentProfileDoesNotExist() {
        when(agentRepository.findByUserEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listingService.createListingForAgent(listingDto(), null, "missing@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Agent profile not found");
    }

    @Test
    void updateListingForAgentKeepsExistingImageWhenNoNewImageIsUploaded() {
        Agent agent = agent("owner@example.com", 10L);
        Listing listing = listing(5L, agent);
        listing.setImage("/uploads/current.jpg");
        ListingRequestDto dto = listingDto();
        dto.setTitle("Updated title");
        dto.setPropertyType("Condo");
        dto.setListingStatus("For Rent");
        when(listingRepository.findById(5L)).thenReturn(Optional.of(listing));
        when(fileStorageService.storeImage(null)).thenReturn(null);
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Listing updated = listingService.updateListingForAgent(5L, dto, null, "owner@example.com");

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getImage()).isEqualTo("/uploads/current.jpg");
        assertThat(updated.getPropertyType()).isEqualTo("Condo");
        assertThat(updated.getListingStatus()).isEqualTo("For Rent");
    }

    @Test
    void updateListingForAgentRejectsListingsOwnedByAnotherAgent() {
        Listing listing = listing(5L, agent("owner@example.com", 10L));
        when(listingRepository.findById(5L)).thenReturn(Optional.of(listing));

        assertThatThrownBy(() -> listingService.updateListingForAgent(5L, listingDto(), null, "other@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("You can only manage listings that you created");
    }

    @Test
    void deleteListingForAgentRemovesBuyerAndAgentRequestsBeforeDelete() {
        Agent owner = agent("owner@example.com", 10L);
        Listing listing = listing(5L, owner);
        Buyer buyer = new Buyer();
        buyer.getRequestedListings().add(listing);
        Agent requestingAgent = agent("other@example.com", 11L);
        requestingAgent.getRequestedListings().add(listing);
        when(listingRepository.findById(5L)).thenReturn(Optional.of(listing));
        when(buyerRepository.findDistinctByRequestedListingsId(5L)).thenReturn(List.of(buyer));
        when(agentRepository.findDistinctByRequestedListingsId(5L)).thenReturn(List.of(requestingAgent));

        listingService.deleteListingForAgent(5L, "owner@example.com");

        assertThat(buyer.getRequestedListings()).doesNotContain(listing);
        assertThat(requestingAgent.getRequestedListings()).doesNotContain(listing);
        verify(buyerRepository).saveAll(List.of(buyer));
        verify(agentRepository).saveAll(List.of(requestingAgent));
        verify(listingRepository).delete(listing);
        verify(listingSearchService).deleteListing(5L);
    }

    @Test
    void createLisingMapsApiDtoToListingResponse() {
        Agent agent = agent("agent@example.com", 4L);
        ListingRequestDto dto = listingDto();
        dto.setAgentId(4L);
        when(agentRepository.findById(4L)).thenReturn(Optional.of(agent));
        when(listingRepository.save(any(Listing.class))).thenAnswer(invocation -> {
            Listing saved = invocation.getArgument(0);
            saved.setId(99L);
            return saved;
        });

        var response = listingService.createLising(dto);

        ArgumentCaptor<Listing> listingCaptor = ArgumentCaptor.forClass(Listing.class);
        verify(listingRepository).save(listingCaptor.capture());
        assertThat(listingCaptor.getValue().getAgent()).isSameAs(agent);
        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getAgentId()).isEqualTo(4L);
        assertThat(response.getAgentName()).isEqualTo("Agent Name");
        verify(listingSearchService).indexListing(any(Listing.class));
    }

    @Test
    void searchListingEntitiesDelegatesToSearchService() {
        Listing listing = listing(1L, agent("a@a.com", 1L));

        when(listingSearchService.searchListings("house", "Apartment"))
                .thenReturn(List.of(listing));

        var result = listingService.searchListingEntities("house", "Apartment");

        assertThat(result).containsExactly(listing);
    }

    private ListingRequestDto listingDto() {
        ListingRequestDto dto = new ListingRequestDto();
        dto.setTitle("Sunny apartment");
        dto.setDescription("Bright rooms near transit");
        dto.setAddress("Bole Road");
        dto.setCity("Addis Ababa");
        dto.setPrice(250000.0);
        dto.setBedrooms(3);
        dto.setBathrooms(2);
        dto.setArea(120.0);
        dto.setPropertyType("Apartment");
        dto.setListingStatus("For Sale");
        return dto;
    }

    private Agent agent(String email, Long id) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("encoded");
        user.setRole(Role.AGENT);
        Agent agent = new Agent();
        agent.setId(id);
        agent.setName("Agent Name");
        agent.setPhone("0911" + id);
        agent.setUser(user);
        return agent;
    }

    private Listing listing(Long id, Agent agent) {
        Listing listing = new Listing();
        listing.setId(id);
        listing.setTitle("Original title");
        listing.setAgent(agent);
        return listing;
    }
}
