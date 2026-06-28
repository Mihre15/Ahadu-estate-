package com.Ahadu_backend.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Ahadu_backend.app.buyer.model.Buyer;

import java.util.List;
import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {

    Optional<Buyer> findByUserId(Long userId);
    Optional<Buyer> findByUserEmail(String email);

    List<Buyer> findByPreferredCity(String preferredCity);

    List<Buyer> findByBudgetMaxGreaterThanEqual(Double budget);

    List<Buyer> findByBudgetMinLessThanEqual(Double budget);

    List<Buyer> findDistinctByRequestedListingsId(Long listingId);
}
