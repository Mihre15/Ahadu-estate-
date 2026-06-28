package com.Ahadu_backend.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Ahadu_backend.app.agent.model.Agent;

import java.util.List;
import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByPhone(String phone);

    Optional<Agent> findByLicenseNumber(String licenseNumber);
    Optional<Agent> findByUserEmail(String email);

    List<Agent> findDistinctByRequestedListingsId(Long listingId);
}
