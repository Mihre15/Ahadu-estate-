package com.Ahadu_backend.app.agent.service;

import java.util.List;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;

public interface IAgenetService {
    AgentResponseDto createAgent(AgentRequestDto dto);

    List<AgentResponseDto> getAllAgents();

    AgentResponseDto getAgentById(Long id);

    AgentResponseDto updateAgent(Long id, AgentRequestDto dto);

    void deleteAgent(Long id);
}
