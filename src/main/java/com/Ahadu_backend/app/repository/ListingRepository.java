package com.Ahadu_backend.app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.Ahadu_backend.app.listing.model.Listing;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    // List<Listing> findByCity(String city);

    Page<Listing> findByCity(String city, Pageable pageable);

    List<Listing> findByPriceBetween(Double minPrice, Double maxPrice);

    List<Listing> findByBedrooms(Integer bedrooms);

    List<Listing> findByBathrooms(Integer bathrooms);

    List<Listing> findByAgentId(Long agentId);

    List<Listing> findByCityAndPriceBetween(
            String city,
            Double minPrice,
            Double maxPrice);
}