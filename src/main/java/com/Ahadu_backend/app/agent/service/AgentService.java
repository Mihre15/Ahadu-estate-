package com.Ahadu_backend.app.agent.service;

import java.util.List;

import com.Ahadu_backend.app.auth.model.User;
import org.springframework.stereotype.Service;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;
import com.Ahadu_backend.app.agent.mapper.AgentMapper;
import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentService implements IAgenetService {
    private final AgentRepository agentRepository;
    private final AgentMapper agentMapper;
    private final UserRepository userRepository;

    @Override
    public AgentResponseDto createAgent(AgentRequestDto dto) {

        Agent agent = agentMapper.toEntity(dto);

        return agentMapper.toResponseDto(
                agentRepository.save(agent));
    }
    public AgentResponseDto createAgentForCurrentUser(AgentRequestDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (agentRepository.findByUserEmail(email).isPresent()) {
            throw new RuntimeException("Agent profile already exists for logged-in user");
        }

        Agent agent = agentMapper.toEntity(dto);
        agent.setUser(user);

        Agent savedAgent = agentRepository.save(agent);

        return agentMapper.toResponseDto(savedAgent);
    }

    public Agent getCurrentAgentEntity(String email) {
        return agentRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Agent profile not found for logged-in user"));
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
    public AgentResponseDto getCurrentAgentProfile(String email) {
        Agent agent = agentRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Agent profile not found for logged-in user"));

        return agentMapper.toResponseDto(agent);
    }

    @Override
    public void deleteAgent(Long id) {

        agentRepository.deleteById(id);
    }
}
