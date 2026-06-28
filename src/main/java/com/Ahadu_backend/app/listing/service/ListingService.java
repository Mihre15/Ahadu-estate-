package com.Ahadu_backend.app.listing.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.Ahadu_backend.app.agent.model.Agent;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingService implements IListingService {
    private final ListingRepository listingRepository;
    private final AgentRepository agentRepository;
    private final BuyerRepository buyerRepository;
    private final ListingMapper listingMapper;
    private final FileStorageService fileStorageService;
    private final ListingSearchService listingSearchService;

    @Override
    public ListingResponseDto createLising(ListingRequestDto dto) {
        Agent agent = agentRepository.findById(dto.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        Listing listing = listingMapper.toEntity(dto, agent);
        Listing savedListing = listingRepository.save(listing);
        listingSearchService.indexListing(savedListing);

        return listingMapper.toResponseDto(savedListing);
    }

    public Listing createListingForAgent(ListingRequestDto dto, MultipartFile imageFile, String email) {
        Agent agent = agentRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Agent profile not found"));

        Listing listing = listingMapper.toEntity(dto, agent);
        listing.setImage(fileStorageService.storeImage(imageFile));
        applyListingDefaults(listing);

        Listing savedListing = listingRepository.save(listing);
        listingSearchService.indexListing(savedListing);
        return savedListing;
    }

    public Listing updateListingForAgent(Long id, ListingRequestDto dto, MultipartFile imageFile, String email) {
        Listing listing = getOwnedListing(id, email);
        String currentImage = listing.getImage();
        listingMapper.updateEntity(listing, dto, listing.getAgent());

        String uploadedImage = fileStorageService.storeImage(imageFile);
        listing.setImage(uploadedImage != null ? uploadedImage : currentImage);
        applyListingDefaults(listing);

        Listing savedListing = listingRepository.save(listing);
        listingSearchService.indexListing(savedListing);
        return savedListing;
    }

    public void deleteListingForAgent(Long id, String email) {
        Listing listing = getOwnedListing(id, email);
        removeListingRequests(listing);
        listingRepository.delete(listing);
        listingSearchService.deleteListing(id);
    }

    public Listing getOwnedListing(Long id, String email) {
        Listing listing = getListingEntityById(id);
        if (listing.getAgent() == null || listing.getAgent().getUser() == null
                || !email.equals(listing.getAgent().getUser().getEmail())) {
            throw new RuntimeException("You can only manage listings that you created");
        }
        return listing;
    }

    @Override
    public List<ListingResponseDto> getAllListings() {
        return listingRepository.findAll()
                .stream()
                .map(listingMapper::toResponseDto)
                .toList();
    }

    public List<Listing> getAllListingEntities() {
        return listingRepository.findAll();
    }

    public List<Listing> searchListingEntities(String query, String propertyType) {
        return listingSearchService.searchListings(query, propertyType);
    }

    public Listing getListingEntityById(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    @Override
    public ListingResponseDto getListingById(Long id) {
        Listing listing = getListingEntityById(id);
        return listingMapper.toResponseDto(listing);
    }

    @Override
    public ListingResponseDto updateListing(Long id, ListingRequestDto dto) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setAddress(dto.getAddress());
        listing.setCity(dto.getCity());
        listing.setPrice(dto.getPrice());
        listing.setBedrooms(dto.getBedrooms());
        listing.setBathrooms(dto.getBathrooms());
        listing.setArea(dto.getArea());
        listing.setImage(dto.getImage());
        listing.setPropertyType(dto.getPropertyType());
        listing.setListingStatus(dto.getListingStatus());

        Listing savedListing = listingRepository.save(listing);
        listingSearchService.indexListing(savedListing);
        return listingMapper.toResponseDto(savedListing);
    }

    @Override
    public void deleteListing(Long id) {
        Listing listing = getListingEntityById(id);
        removeListingRequests(listing);
        listingRepository.delete(listing);
        listingSearchService.deleteListing(id);
    }

    @Override
    public List<ListingResponseDto> getListingsByCity(String city) {
        return listingRepository.findByCity(city)
                .stream()
                .map(listingMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<ListingResponseDto> getListingsByAgent(Long agentId) {
        return listingRepository.findByAgentId(agentId)
                .stream()
                .map(listingMapper::toResponseDto)
                .toList();
    }

    private void applyListingDefaults(Listing listing) {
        if (listing.getListingStatus() == null || listing.getListingStatus().isBlank()) {
            listing.setListingStatus("For Sale");
        }
        if (listing.getPropertyType() == null || listing.getPropertyType().isBlank()) {
            listing.setPropertyType("Residential");
        }
    }

    private void removeListingRequests(Listing listing) {
        List<Buyer> buyers = buyerRepository.findDistinctByRequestedListingsId(listing.getId());
        buyers.forEach(buyer -> buyer.getRequestedListings().remove(listing));
        buyerRepository.saveAll(buyers);

        List<Agent> agents = agentRepository.findDistinctByRequestedListingsId(listing.getId());
        agents.forEach(agent -> agent.getRequestedListings().remove(listing));
        agentRepository.saveAll(agents);
    }
}
