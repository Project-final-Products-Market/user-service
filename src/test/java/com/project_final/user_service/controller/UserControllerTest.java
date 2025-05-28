package com.project_final.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_final.user_service.dto.OrderDTO;
import com.project_final.user_service.model.User;
import com.project_final.user_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("User Controller Unit Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<OrderDTO> testOrders;

    @BeforeEach
    void setUp() {
        testUser = new User("Juan Pérez", "juan@example.com");
        testUser.setId(1L);

        OrderDTO order1 = new OrderDTO(1L, 1L, 2, new BigDecimal("100.00"), LocalDateTime.now());
        OrderDTO order2 = new OrderDTO(2L, 2L, 1, new BigDecimal("50.00"), LocalDateTime.now());
        testOrders = Arrays.asList(order1, order2);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        verify(userService).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should return bad request when user creation fails")
    void shouldReturnBadRequestWhenUserCreationFails() throws Exception {
        // Given
        when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("Ya existe un usuario con ese email"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isBadRequest());

        verify(userService).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should get all users")
    void shouldGetAllUsers() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser, new User("Ana García", "ana@example.com"));
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].name").value("Ana García"));

        verify(userService).getAllUsers();
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        verify(userService).getUserById(1L);
    }

    @Test
    @DisplayName("Should return not found when user doesn't exist")
    void shouldReturnNotFoundWhenUserDoesntExist() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(999L);
    }

    @Test
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() throws Exception {
        // Given
        when(userService.getUserByEmail("juan@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/email/juan@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        verify(userService).getUserByEmail("juan@example.com");
    }

    @Test
    @DisplayName("Should return not found when user email doesn't exist")
    void shouldReturnNotFoundWhenUserEmailDoesntExist() throws Exception {
        // Given
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());

        verify(userService).getUserByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        // Given
        User updatedUser = new User("Juan Carlos Pérez", "juancarlos@example.com");
        updatedUser.setId(1L);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Juan Carlos Pérez"))
                .andExpect(jsonPath("$.email").value("juancarlos@example.com"));

        verify(userService).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("Should return not found when updating non-existent user")
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
        // Given
        when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        // When & Then
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound());

        verify(userService).updateUser(eq(999L), any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent user")
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        // Given
        doThrow(new RuntimeException("Usuario no encontrado")).when(userService).deleteUser(999L);

        // When & Then
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(999L);
    }

    @Test
    @DisplayName("Should search users by name")
    void shouldSearchUsersByName() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.searchUsersByName("Juan")).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .param("name", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"));

        verify(userService).searchUsersByName("Juan");
    }

    @Test
    @DisplayName("Should get user orders successfully")
    void shouldGetUserOrdersSuccessfully() throws Exception {
        // Given
        when(userService.getUserOrders(1L)).thenReturn(testOrders);

        // When & Then
        mockMvc.perform(get("/api/users/1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(userService).getUserOrders(1L);
    }

    @Test
    @DisplayName("Should return service unavailable when order service fails")
    void shouldReturnServiceUnavailableWhenOrderServiceFails() throws Exception {
        // Given
        when(userService.getUserOrders(1L))
                .thenThrow(new RuntimeException("Error al obtener órdenes"));

        // When & Then
        mockMvc.perform(get("/api/users/1/orders"))
                .andExpect(status().isServiceUnavailable());

        verify(userService).getUserOrders(1L);
    }

    @Test
    @DisplayName("Should get total users count")
    void shouldGetTotalUsersCount() throws Exception {
        // Given
        when(userService.getTotalUsers()).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/users/stats/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));

        verify(userService).getTotalUsers();
    }
}
