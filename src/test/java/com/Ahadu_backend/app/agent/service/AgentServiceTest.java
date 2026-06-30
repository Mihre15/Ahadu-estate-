package com.Ahadu_backend.app.agent.service;

import com.Ahadu_backend.app.agent.dto.AgentRequestDto;
import com.Ahadu_backend.app.agent.dto.AgentResponseDto;
import com.Ahadu_backend.app.agent.mapper.AgentMapper;
import com.Ahadu_backend.app.agent.model.Agent;
import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.repository.AgentRepository;
import com.Ahadu_backend.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private AgentMapper agentMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AgentService agentService;

    private Agent agent;
    private AgentRequestDto requestDto;
    private AgentResponseDto responseDto;
    private User user;

    @BeforeEach
    void setUp() {

        requestDto = new AgentRequestDto();
        requestDto.setName("John");
        requestDto.setPhone("0911111111");
        requestDto.setAgencyName("Best Agency");
        requestDto.setLicenseNumber("LIC123");

        agent = new Agent();
        agent.setId(1L);
        agent.setName("John");
        agent.setPhone("0911111111");
        agent.setAgencyName("Best Agency");
        agent.setLicenseNumber("LIC123");

        responseDto = new AgentResponseDto();
        responseDto.setId(1L);
        responseDto.setName("John");

        user = new User();
        user.setEmail("john@test.com");
    }

    @Test
    void createAgent_ShouldReturnResponseDto() {

        when(agentMapper.toEntity(requestDto)).thenReturn(agent);
        when(agentRepository.save(agent)).thenReturn(agent);
        when(agentMapper.toResponseDto(agent)).thenReturn(responseDto);

        AgentResponseDto result = agentService.createAgent(requestDto);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());

        verify(agentMapper).toEntity(requestDto);
        verify(agentRepository).save(agent);
        verify(agentMapper).toResponseDto(agent);
    }

    @Test
    void createAgentForCurrentUser_ShouldCreateSuccessfully() {

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        when(agentRepository.findByUserEmail("john@test.com"))
                .thenReturn(Optional.empty());

        when(agentMapper.toEntity(requestDto))
                .thenReturn(agent);

        when(agentRepository.save(agent))
                .thenReturn(agent);

        when(agentMapper.toResponseDto(agent))
                .thenReturn(responseDto);

        AgentResponseDto result = agentService.createAgentForCurrentUser(requestDto, "john@test.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());

        assertEquals(user, agent.getUser());

        verify(agentRepository).save(agent);
    }

    @Test
    void createAgentForCurrentUser_ShouldThrowIfUserNotFound() {

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> agentService.createAgentForCurrentUser(requestDto, "john@test.com"));

        assertEquals("User not found", ex.getMessage());

        verify(agentRepository, never()).save(any());
    }

    @Test
    void createAgentForCurrentUser_ShouldThrowIfAgentAlreadyExists() {

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        when(agentRepository.findByUserEmail("john@test.com"))
                .thenReturn(Optional.of(agent));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> agentService.createAgentForCurrentUser(requestDto, "john@test.com"));

        assertEquals("Agent profile already exists for logged-in user", ex.getMessage());

        verify(agentRepository, never()).save(any());
    }

    @Test
    void getCurrentAgentEntity_ShouldReturnAgent() {

        when(agentRepository.findByUserEmail("john@test.com"))
                .thenReturn(Optional.of(agent));

        Agent result = agentService.getCurrentAgentEntity("john@test.com");

        assertEquals(agent, result);
    }

    @Test
    void getCurrentAgentEntity_ShouldThrowWhenNotFound() {

        when(agentRepository.findByUserEmail("john@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> agentService.getCurrentAgentEntity("john@test.com"));

        assertEquals("Agent profile not found for logged-in user", ex.getMessage());
    }

    @Test
    void getAllAgents_ShouldReturnList() {

        when(agentRepository.findAll())
                .thenReturn(List.of(agent));

        when(agentMapper.toResponseDto(agent))
                .thenReturn(responseDto);

        List<AgentResponseDto> result = agentService.getAllAgents();

        assertEquals(1, result.size());
        verify(agentRepository).findAll();
    }

    @Test
    void getAgentById_ShouldReturnAgent() {

        when(agentRepository.findById(1L))
                .thenReturn(Optional.of(agent));

        when(agentMapper.toResponseDto(agent))
                .thenReturn(responseDto);

        AgentResponseDto result = agentService.getAgentById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getAgentById_ShouldThrowWhenNotFound() {

        when(agentRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> agentService.getAgentById(1L));

        assertEquals("Agent not found", ex.getMessage());
    }

    @Test
    void updateAgent_ShouldUpdateSuccessfully() {

        when(agentRepository.findById(1L))
                .thenReturn(Optional.of(agent));

        when(agentRepository.save(agent))
                .thenReturn(agent);

        when(agentMapper.toResponseDto(agent))
                .thenReturn(responseDto);

        AgentResponseDto result = agentService.updateAgent(1L, requestDto);

        assertEquals("John", agent.getName());
        assertEquals("0911111111", agent.getPhone());

        verify(agentRepository).save(agent);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateAgent_ShouldThrowWhenAgentNotFound() {

        when(agentRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> agentService.updateAgent(1L, requestDto));

        assertEquals("Agent not found", ex.getMessage());
    }

    @Test
    void getCurrentAgentProfile_ShouldReturnProfile() {

        when(agentRepository.findByUserEmail("john@test.com"))
                .thenReturn(Optional.of(agent));

        when(agentMapper.toResponseDto(agent))
                .thenReturn(responseDto);

        AgentResponseDto result = agentService.getCurrentAgentProfile("john@test.com");

        assertEquals(1L, result.getId());
    }

    @Test
    void getCurrentAgentProfile_ShouldThrowWhenNotFound() {

        when(agentRepository.findByUserEmail("john@test.com"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> agentService.getCurrentAgentProfile("john@test.com"));

        assertEquals("Agent profile not found for logged-in user", ex.getMessage());
    }

    @Test
    void deleteAgent_ShouldCallRepository() {

        doNothing().when(agentRepository).deleteById(1L);

        agentService.deleteAgent(1L);

        verify(agentRepository).deleteById(1L);
    }
}