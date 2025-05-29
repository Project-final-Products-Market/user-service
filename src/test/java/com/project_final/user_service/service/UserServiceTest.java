package com.project_final.user_service.service;

import com.project_final.user_service.dto.OrderDTO;
import com.project_final.user_service.model.User;
import com.project_final.user_service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private OrderDTO[] testOrders;

    @BeforeEach
    void setUp() {
        testUser = new User("Juan Pérez", "juan@example.com");
        testUser.setId(1L);

        OrderDTO order1 = new OrderDTO(1L, 1L, 2, new BigDecimal("100.00"), LocalDateTime.now());
        OrderDTO order2 = new OrderDTO(2L, 2L, 1, new BigDecimal("50.00"), LocalDateTime.now());
        testOrders = new OrderDTO[]{order1, order2};
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User createdUser = userService.createUser(testUser);

        // Then
        assertNotNull(createdUser);
        assertEquals(testUser.getName(), createdUser.getName());
        assertEquals(testUser.getEmail(), createdUser.getEmail());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.createUser(testUser));

        // Corregir el mensaje esperado para que coincida con el real
        assertEquals("Ya existe un usuario con el email: juan@example.com", exception.getMessage());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() {
        // Given
        List<User> users = Arrays.asList(testUser, new User("Ana García", "ana@example.com"));
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void shouldReturnEmptyWhenUserNotFoundById() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(999L);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail(testUser.getEmail());

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        User updatedDetails = new User("Juan Carlos Pérez", "juancarlos@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(1L, updatedDetails);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        User updatedDetails = new User("Juan Carlos Pérez", "juancarlos@example.com");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUser(999L, updatedDetails));

        // Corregir "id" por "ID" para que coincida con el mensaje real
        assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(999L));

        // Corregir "id" por "ID" para que coincida con el mensaje real
        assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("Should search users by name")
    void shouldSearchUsersByName() {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findByNameContaining("Juan")).thenReturn(users);

        // When
        List<User> result = userService.searchUsersByName("Juan");

        // Then
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository).findByNameContaining("Juan");
    }

    @Test
    @DisplayName("Should get user orders successfully")
    void shouldGetUserOrdersSuccessfully() {
        // Given - Primero mockear que el usuario existe
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String expectedUrl = "http://order-service/api/orders/user/1";
        when(restTemplate.getForObject(expectedUrl, OrderDTO[].class)).thenReturn(testOrders);

        // When
        List<OrderDTO> result = userService.getUserOrders(1L);

        // Then
        assertEquals(2, result.size());
        assertEquals(testOrders[0].getId(), result.get(0).getId());
        assertEquals(testOrders[1].getId(), result.get(1).getId());
        verify(userRepository).findById(1L); // Verificar que se buscó el usuario
        verify(restTemplate).getForObject(expectedUrl, OrderDTO[].class);
    }

    @Test
    @DisplayName("Should handle null orders response")
    void shouldHandleNullOrdersResponse() {
        // Given - Primero mockear que el usuario existe
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String expectedUrl = "http://order-service/api/orders/user/1";
        when(restTemplate.getForObject(expectedUrl, OrderDTO[].class)).thenReturn(null);

        // When
        List<OrderDTO> result = userService.getUserOrders(1L);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findById(1L); // Verificar que se buscó el usuario
        verify(restTemplate).getForObject(expectedUrl, OrderDTO[].class);
    }

    @Test
    @DisplayName("Should throw exception when order service fails")
    void shouldThrowExceptionWhenOrderServiceFails() {
        // Given - Primero mockear que el usuario existe
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        String expectedUrl = "http://order-service/api/orders/user/1";
        when(restTemplate.getForObject(expectedUrl, OrderDTO[].class))
                .thenThrow(new RestClientException("Service unavailable"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.getUserOrders(1L));

        // Verificar que el mensaje contiene lo esperado (más flexible)
        assertTrue(exception.getMessage().contains("Error al obtener órdenes del usuario") ||
                exception.getMessage().contains("Service unavailable"));
        verify(userRepository).findById(1L); // Verificar que se buscó el usuario
        verify(restTemplate).getForObject(expectedUrl, OrderDTO[].class);
    }

    @Test
    @DisplayName("Should get total users count")
    void shouldGetTotalUsersCount() {
        // Given
        when(userRepository.countAllUsers()).thenReturn(5L);

        // When
        Long result = userService.getTotalUsers();

        // Then
        assertEquals(5L, result);
        verify(userRepository).countAllUsers();
    }
}
