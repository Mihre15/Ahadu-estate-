package com.Ahadu_backend.app.listing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.listing.dto.ListingRequestDto;
import com.Ahadu_backend.app.listing.dto.ListingResponseDto;
import com.Ahadu_backend.app.listing.mapper.ListingMapper;
import com.Ahadu_backend.app.listing.model.Listing;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.ListingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingService implements IListingService {
    private final ListingRepository listingRepository;
    private final AgentRepository agentRepository;
    private final ListingMapper listingMapper;

    @Override
    public ListingResponseDto createLising(ListingRequestDto dto) {

        Agent agent = agentRepository.findById(dto.getAgentId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        Listing listing = listingMapper.toEntity(dto, agent);

        listing.setAgent(agent);

        return listingMapper.toResponseDto(
                listingRepository.save(listing));
    }

    @Override
    public List<ListingResponseDto> getAllListings() {

        return listingRepository.findAll()
                .stream()
                .map(listingMapper::toResponseDto)
                .toList();
    }

    @Override
    public ListingResponseDto getListingById(Long id) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

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

        return listingMapper.toResponseDto(
                listingRepository.save(listing));
    }

    @Override
    public void deleteListing(Long id) {

        listingRepository.deleteById(id);
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
}
