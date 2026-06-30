package com.Ahadu_backend.app.buyer.service;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.dto.BuyerRequestDto;
import com.Ahadu_backend.app.buyer.dto.BuyerResponseDto;
import com.Ahadu_backend.app.buyer.mapper.BuyerMapper;
import com.Ahadu_backend.app.buyer.model.Buyer;
import com.Ahadu_backend.app.repository.BuyerRepository;
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
class BuyerServiceTest {

    @Mock
    private BuyerRepository buyerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BuyerMapper buyerMapper;

    @InjectMocks
    private BuyerService buyerService;

    private BuyerRequestDto requestDto;
    private Buyer buyer;
    private BuyerResponseDto responseDto;
    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("buyer@test.com");

        requestDto = new BuyerRequestDto();
        requestDto.setUserId(1L);
        requestDto.setName("John");
        requestDto.setPhone("0912345678");
        requestDto.setPreferredCity("Addis Ababa");
        requestDto.setBudgetMin(100000.0);
        requestDto.setBudgetMax(500000.0);

        buyer = new Buyer();
        buyer.setId(1L);
        buyer.setName("John");
        buyer.setPhone("0912345678");
        buyer.setPreferredCity("Addis Ababa");
        buyer.setBudgetMin(100000.0);
        buyer.setBudgetMax(500000.0);
        buyer.setUser(user);

        responseDto = new BuyerResponseDto();
        responseDto.setId(1L);
        responseDto.setName("John");
    }

    @Test
    void createBuyer_ShouldCreateBuyerSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(buyerMapper.toEntity(requestDto, user)).thenReturn(buyer);
        when(buyerRepository.save(buyer)).thenReturn(buyer);
        when(buyerMapper.toResponseDto(buyer)).thenReturn(responseDto);

        BuyerResponseDto result = buyerService.createBuyer(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertSame(user, buyer.getUser());

        verify(userRepository).findById(1L);
        verify(buyerMapper).toEntity(requestDto, user);
        verify(buyerRepository).save(buyer);
        verify(buyerMapper).toResponseDto(buyer);
    }

    @Test
    void createBuyer_ShouldThrow_WhenUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> buyerService.createBuyer(requestDto));

        assertEquals("User not found", ex.getMessage());

        verify(buyerRepository, never()).save(any());
        verify(buyerMapper, never()).toEntity(any(), any());
    }

    @Test
    void getAllBuyers_ShouldReturnList() {

        when(buyerRepository.findAll()).thenReturn(List.of(buyer));
        when(buyerMapper.toResponseDto(buyer)).thenReturn(responseDto);

        List<BuyerResponseDto> result = buyerService.getAllBuyers();

        assertEquals(1, result.size());
        assertEquals(responseDto, result.get(0));

        verify(buyerRepository).findAll();
        verify(buyerMapper).toResponseDto(buyer);
    }

    @Test
    void getBuyerById_ShouldReturnBuyer() {

        when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(buyerMapper.toResponseDto(buyer)).thenReturn(responseDto);

        BuyerResponseDto result = buyerService.getBuyerById(1L);

        assertEquals(responseDto, result);

        verify(buyerRepository).findById(1L);
    }

    @Test
    void getBuyerById_ShouldThrow_WhenBuyerNotFound() {

        when(buyerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> buyerService.getBuyerById(1L));

        assertEquals("Buyer not found", ex.getMessage());
    }

    @Test
    void getBuyerByUserId_ShouldReturnBuyer() {

        when(buyerRepository.findByUserId(1L)).thenReturn(Optional.of(buyer));
        when(buyerMapper.toResponseDto(buyer)).thenReturn(responseDto);

        BuyerResponseDto result = buyerService.getBuyerByUserId(1L);

        assertEquals(responseDto, result);

        verify(buyerRepository).findByUserId(1L);
    }

    @Test
    void getBuyerByUserId_ShouldThrow_WhenBuyerNotFound() {

        when(buyerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> buyerService.getBuyerByUserId(1L));

        assertEquals("Buyer not found", ex.getMessage());
    }

    @Test
    void updateBuyer_ShouldUpdateBuyerSuccessfully() {

        when(buyerRepository.findById(1L)).thenReturn(Optional.of(buyer));
        when(buyerRepository.save(buyer)).thenReturn(buyer);
        when(buyerMapper.toResponseDto(buyer)).thenReturn(responseDto);

        BuyerResponseDto result = buyerService.updateBuyer(1L, requestDto);

        assertEquals("John", buyer.getName());
        assertEquals("0912345678", buyer.getPhone());
        assertEquals("Addis Ababa", buyer.getPreferredCity());
        assertEquals(100000.0, buyer.getBudgetMin());
        assertEquals(500000.0, buyer.getBudgetMax());

        assertEquals(responseDto, result);

        verify(buyerRepository).save(buyer);
    }

    @Test
    void updateBuyer_ShouldThrow_WhenBuyerNotFound() {

        when(buyerRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> buyerService.updateBuyer(1L, requestDto));

        assertEquals("Buyer not found", ex.getMessage());

        verify(buyerRepository, never()).save(any());
    }

    @Test
    void deleteBuyer_ShouldDeleteBuyer() {

        doNothing().when(buyerRepository).deleteById(1L);

        buyerService.deleteBuyer(1L);

        verify(buyerRepository).deleteById(1L);
    }
}