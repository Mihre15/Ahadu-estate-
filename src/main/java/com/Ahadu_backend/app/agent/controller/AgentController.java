package com.Ahadu_backend.app.agent.controller;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;
import com.Ahadu_backend.app.agent.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;
    @PostMapping("/me")
    public ResponseEntity<AgentResponseDto> createCurrentAgentProfile(
            @RequestBody AgentRequestDto dto,
            Authentication authentication) {

        String email = authentication.getName();

        AgentResponseDto createdAgent = agentService.createAgentForCurrentUser(dto, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdAgent);
    }

    @PostMapping
    public ResponseEntity<AgentResponseDto> createAgent(
            @RequestBody AgentRequestDto dto) {
        AgentResponseDto createdAgent = agentService.createAgent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAgent);
    }

    @GetMapping
    public ResponseEntity<List<AgentResponseDto>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgentResponseDto> getAgentById(
            @PathVariable Long id) {
        return ResponseEntity.ok(agentService.getAgentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgentResponseDto> updateAgent(
            @PathVariable Long id,
            @RequestBody AgentRequestDto dto) {
        return ResponseEntity.ok(agentService.updateAgent(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgent(
            @PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }
}
