package com.Ahadu_backend.app.buyer.mapper;

import com.Ahadu_backend.app.auth.model.User;
import com.Ahadu_backend.app.buyer.dto.BuyerRequestDto;
import com.Ahadu_backend.app.buyer.dto.BuyerResponseDto;
import com.Ahadu_backend.app.buyer.model.Buyer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuyerMapperTest {

    private BuyerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BuyerMapper();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // Arrange
        BuyerRequestDto request = new BuyerRequestDto();
        request.setName("John Doe");
        request.setPhone("0912345678");
        request.setPreferredCity("Addis Ababa");
        request.setBudgetMin(100000.0);
        request.setBudgetMax(500000.0);

        User user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");

        // Act
        Buyer buyer = mapper.toEntity(request, user);

        // Assert
        assertNotNull(buyer);
        assertNull(buyer.getId());

        assertEquals(request.getName(), buyer.getName());
        assertEquals(request.getPhone(), buyer.getPhone());
        assertEquals(request.getPreferredCity(), buyer.getPreferredCity());
        assertEquals(request.getBudgetMin(), buyer.getBudgetMin());
        assertEquals(request.getBudgetMax(), buyer.getBudgetMax());

        assertSame(user, buyer.getUser());

        // The mapper should leave the collection initialized
        assertNotNull(buyer.getRequestedListings());
        assertTrue(buyer.getRequestedListings().isEmpty());
    }

    @Test
    void toResponseDto_ShouldMapAllFields_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setId(10L);
        user.setEmail("buyer@test.com");

        Buyer buyer = new Buyer();
        buyer.setId(1L);
        buyer.setName("John Doe");
        buyer.setPhone("0912345678");
        buyer.setPreferredCity("Addis Ababa");
        buyer.setBudgetMin(100000.0);
        buyer.setBudgetMax(500000.0);
        buyer.setUser(user);

        // Act
        BuyerResponseDto dto = mapper.toResponseDto(buyer);

        // Assert
        assertNotNull(dto);

        assertEquals(buyer.getId(), dto.getId());
        assertEquals(buyer.getName(), dto.getName());
        assertEquals(buyer.getPhone(), dto.getPhone());
        assertEquals(buyer.getPreferredCity(), dto.getPreferredCity());
        assertEquals(buyer.getBudgetMin(), dto.getBudgetMin());
        assertEquals(buyer.getBudgetMax(), dto.getBudgetMax());

        // Covers the true branch
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void toResponseDto_ShouldMapFields_WhenUserIsNull() {
        // Arrange
        Buyer buyer = new Buyer();
        buyer.setId(2L);
        buyer.setName("Jane Doe");
        buyer.setPhone("0999999999");
        buyer.setPreferredCity("Hawassa");
        buyer.setBudgetMin(200000.0);
        buyer.setBudgetMax(700000.0);
        buyer.setUser(null);

        // Act
        BuyerResponseDto dto = mapper.toResponseDto(buyer);

        // Assert
        assertNotNull(dto);

        assertEquals(buyer.getId(), dto.getId());
        assertEquals(buyer.getName(), dto.getName());
        assertEquals(buyer.getPhone(), dto.getPhone());
        assertEquals(buyer.getPreferredCity(), dto.getPreferredCity());
        assertEquals(buyer.getBudgetMin(), dto.getBudgetMin());
        assertEquals(buyer.getBudgetMax(), dto.getBudgetMax());

        // Covers the false branch
        assertNull(dto.getUserId());
        assertNull(dto.getEmail());
    }

    @Test
    void updateEntity_ShouldUpdateAllFields() {
        // Arrange
        Buyer buyer = new Buyer();
        buyer.setId(5L);
        buyer.setName("Old Name");
        buyer.setPhone("0900000000");
        buyer.setPreferredCity("Old City");
        buyer.setBudgetMin(50000.0);
        buyer.setBudgetMax(100000.0);

        User user = new User();
        user.setId(2L);
        user.setEmail("new@test.com");

        BuyerRequestDto request = new BuyerRequestDto();
        request.setName("New Name");
        request.setPhone("0911111111");
        request.setPreferredCity("Addis Ababa");
        request.setBudgetMin(300000.0);
        request.setBudgetMax(600000.0);

        // Act
        mapper.updateEntity(buyer, request, user);

        // Assert
        // Existing ID should remain unchanged
        assertEquals(5L, buyer.getId());

        assertEquals(request.getName(), buyer.getName());
        assertEquals(request.getPhone(), buyer.getPhone());
        assertEquals(request.getPreferredCity(), buyer.getPreferredCity());
        assertEquals(request.getBudgetMin(), buyer.getBudgetMin());
        assertEquals(request.getBudgetMax(), buyer.getBudgetMax());

        assertSame(user, buyer.getUser());
    }
}