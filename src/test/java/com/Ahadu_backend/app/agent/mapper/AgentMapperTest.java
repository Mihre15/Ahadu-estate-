package com.Ahadu_backend.app.agent.mapper;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;
import com.Ahadu_backend.app.agent.model.Agent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AgentMapperTest {

    private AgentMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AgentMapper();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Arrange
        AgentRequestDto request = new AgentRequestDto();
        request.setName("John Doe");
        request.setPhone("0912345678");
        request.setAgencyName("ABC Realty");
        request.setLicenseNumber("LIC-123");

        // Act
        Agent agent = mapper.toEntity(request);

        // Assert
        assertNotNull(agent);
        assertEquals(request.getName(), agent.getName());
        assertEquals(request.getPhone(), agent.getPhone());
        assertEquals(request.getAgencyName(), agent.getAgencyName());
        assertEquals(request.getLicenseNumber(), agent.getLicenseNumber());

        // id should not be set by the mapper
        assertNull(agent.getId());
    }

    @Test
    void toResponseDto_ShouldMapAllFields() {
        // Arrange
        Agent agent = new Agent();
        agent.setId(1L);
        agent.setName("John Doe");
        agent.setPhone("0912345678");
        agent.setAgencyName("ABC Realty");
        agent.setLicenseNumber("LIC-123");

        // Act
        AgentResponseDto response = mapper.toResponseDto(agent);

        // Assert
        assertNotNull(response);
        assertEquals(agent.getId(), response.getId());
        assertEquals(agent.getName(), response.getName());
        assertEquals(agent.getPhone(), response.getPhone());
        assertEquals(agent.getAgencyName(), response.getAgencyName());
        assertEquals(agent.getLicenseNumber(), response.getLicenseNumber());
    }

    @Test
    void updateEntity_ShouldUpdateAllFields() {
        // Arrange
        Agent agent = new Agent();
        agent.setId(1L);
        agent.setName("Old Name");
        agent.setPhone("0900000000");
        agent.setAgencyName("Old Agency");
        agent.setLicenseNumber("OLD-LIC");

        AgentRequestDto request = new AgentRequestDto();
        request.setName("New Name");
        request.setPhone("0999999999");
        request.setAgencyName("New Agency");
        request.setLicenseNumber("NEW-LIC");

        // Act
        mapper.updateEntity(agent, request);

        // Assert
        assertEquals(1L, agent.getId()); // id should remain unchanged
        assertEquals(request.getName(), agent.getName());
        assertEquals(request.getPhone(), agent.getPhone());
        assertEquals(request.getAgencyName(), agent.getAgencyName());
        assertEquals(request.getLicenseNumber(), agent.getLicenseNumber());
    }
}