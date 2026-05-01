package com.Ahadu_backend.app.agent.mapper;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;
import com.Ahadu_backend.app.agent.model.Agent;
import org.springframework.stereotype.Component;

@Component
public class AgentMapper {
    public Agent toEntity(AgentRequestDto dto) {
        Agent agent = new Agent();

        agent.setName(dto.getName());
        agent.setPhone(dto.getPhone());
        agent.setAgencyName(dto.getAgencyName());
        agent.setLicenseNumber(dto.getLicenseNumber());

        return agent;
    }

    public AgentResponseDto toResponseDto(Agent agent) {
        AgentResponseDto dto = new AgentResponseDto();

        dto.setId(agent.getId());
        dto.setName(agent.getName());
        dto.setPhone(agent.getPhone());
        dto.setAgencyName(agent.getAgencyName());
        dto.setLicenseNumber(agent.getLicenseNumber());

        return dto;
    }

    public void updateEntity(Agent agent, AgentRequestDto dto) {
        agent.setName(dto.getName());
        agent.setPhone(dto.getPhone());
        agent.setAgencyName(dto.getAgencyName());
        agent.setLicenseNumber(dto.getLicenseNumber());
    }
}
