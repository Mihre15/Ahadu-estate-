package com.Ahadu_backend.app.agent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;
import com.Ahadu_backend.app.agent.mapper.AgentMapper;
import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.repository.AgentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentService implements IAgenetService {
    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;

    @Override
    public AgentResponseDto createAgent(AgentRequestDto dto) {

        Agent agent = agentMapper.toEntity(dto);

        return agentMapper.toResponseDto(
                agentRepository.save(agent));
    }

    @Override
    public List<AgentResponseDto> getAllAgents() {

        return agentRepository.findAll()
                .stream()
                .map(agentMapper::toResponseDto)
                .toList();
    }

    @Override
    public AgentResponseDto getAgentById(Long id) {

        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        return agentMapper.toResponseDto(agent);
    }

    @Override
    public AgentResponseDto updateAgent(Long id, AgentRequestDto dto) {

        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        agent.setName(dto.getName());
        agent.setPhone(dto.getPhone());
        agent.setAgencyName(dto.getAgencyName());
        agent.setLicenseNumber(dto.getLicenseNumber());

        return agentMapper.toResponseDto(
                agentRepository.save(agent));
    }

    @Override
    public void deleteAgent(Long id) {

        agentRepository.deleteById(id);
    }
}
