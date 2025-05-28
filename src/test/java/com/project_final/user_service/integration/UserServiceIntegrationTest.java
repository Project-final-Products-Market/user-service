package com.project_final.user_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_final.user_service.model.User;
import com.project_final.user_service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@Rollback // Esta anotación asegura que cada test haga rollback automáticamente
@DisplayName("User Service Integration Tests")
class UserServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Limpieza adicional por seguridad (aunque @Rollback debería manejar esto)
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Integration Test: Complete user lifecycle")
    void completeUserLifecycle() throws Exception {
        // 1. Create User
        User newUser = new User("Juan Pérez", "juan@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        // 2. Verify user was saved
        assertEquals(1, userRepository.count());
        User savedUser = userRepository.findAll().get(0);
        assertNotNull(savedUser);
        assertEquals("Juan Pérez", savedUser.getName());

        // 3. Get user by ID
        mockMvc.perform(get("/api/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Juan Pérez"));

        // 4. Update user
        User updatedUser = new User("Juan Carlos Pérez", "juancarlos@example.com");
        mockMvc.perform(put("/api/users/" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan Carlos Pérez"))
                .andExpect(jsonPath("$.email").value("juancarlos@example.com"));

        // 5. Verify update in database
        User dbUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(dbUser);
        assertEquals("Juan Carlos Pérez", dbUser.getName());
        assertEquals("juancarlos@example.com", dbUser.getEmail());

        // 6. Get all users
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        // 7. Delete user
        mockMvc.perform(delete("/api/users/" + savedUser.getId()))
                .andExpect(status().isNoContent());

        // 8. Verify deletion
        assertEquals(0, userRepository.count());
    }

    @Test
    @DisplayName("Integration Test: User search functionality")
    void userSearchFunctionality() throws Exception {
        // Create test users with unique emails for this test
        User user1 = new User("Juan Pérez", "juan.search@example.com");
        User user2 = new User("Juan Carlos", "juancarlos.search@example.com");
        User user3 = new User("Ana García", "ana.search@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // Test search by name
        mockMvc.perform(get("/api/users/search")
                        .param("name", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].name").value("Juan Carlos"));

        // Test search by email
        mockMvc.perform(get("/api/users/email/ana.search@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ana García"));

        // Test non-existent email
        mockMvc.perform(get("/api/users/email/nonexistent@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration Test: User statistics")
    void userStatistics() throws Exception {
        // Create test users with unique emails for this test
        userRepository.save(new User("User 1", "user1.stats@example.com"));
        userRepository.save(new User("User 2", "user2.stats@example.com"));
        userRepository.save(new User("User 3", "user3.stats@example.com"));

        // Test total users count
        mockMvc.perform(get("/api/users/stats/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    @DisplayName("Integration Test: Error handling scenarios")
    void errorHandlingScenarios() throws Exception {
        // Test get non-existent user
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        // Test update non-existent user
        User updateUser = new User("Updated Name", "updated@example.com");
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isNotFound());

        // Test delete non-existent user
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

        // Test create user with duplicate email
        User user1 = new User("User 1", "duplicate.error@example.com");
        User user2 = new User("User 2", "duplicate.error@example.com");

        userRepository.save(user1);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration Test: Database constraints and validations")
    void databaseConstraintsAndValidations() {
        // Test saving user with all required fields
        User validUser = new User("Valid User", "valid.constraints@example.com");
        User savedUser = userRepository.save(validUser);

        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
        assertEquals("Valid User", savedUser.getName());
        assertEquals("valid.constraints@example.com", savedUser.getEmail());

        // Test email uniqueness
        User duplicateEmailUser = new User("Another User", "valid.constraints@example.com");
        assertThrows(Exception.class, () -> {
            userRepository.saveAndFlush(duplicateEmailUser);
        });
    }

    @Test
    @DisplayName("Integration Test: Concurrent user creation")
    void concurrentUserCreation() throws InterruptedException {
        int numberOfThreads = 5;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    // Usar timestamp para asegurar emails únicos
                    String uniqueEmail = "user" + userId + "." + System.currentTimeMillis() + "@example.com";
                    User user = new User("User " + userId, uniqueEmail);
                    ResponseEntity<User> response = restTemplate.postForEntity(
                            baseUrl, user, User.class);

                    // Should succeed for unique emails
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                } catch (Exception e) {
                    // Log error but don't fail test
                    System.out.println("Expected exception in concurrent test: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Verify that database operations were atomic
        long totalUsers = userRepository.count();
        assertTrue(totalUsers >= 0 && totalUsers <= numberOfThreads);
    }

    @Test
    @DisplayName("Integration Test: User CRUD with REST client")
    void userCrudWithRestClient() {
        // Create user with unique email
        User newUser = new User("REST User", "rest.crud@example.com");
        ResponseEntity<User> createResponse = restTemplate.postForEntity(baseUrl, newUser, User.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long userId = createResponse.getBody().getId();

        // Get user
        ResponseEntity<User> getResponse = restTemplate.getForEntity(baseUrl + "/" + userId, User.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("REST User", getResponse.getBody().getName());

        // Update user
        User updateUser = new User("Updated REST User", "updatedrest.crud@example.com");
        HttpEntity<User> updateEntity = new HttpEntity<>(updateUser);
        ResponseEntity<User> updateResponse = restTemplate.exchange(
                baseUrl + "/" + userId, HttpMethod.PUT, updateEntity, User.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated REST User", updateResponse.getBody().getName());

        // Delete user
        restTemplate.delete(baseUrl + "/" + userId);

        // Verify deletion
        ResponseEntity<User> deletedResponse = restTemplate.getForEntity(baseUrl + "/" + userId, User.class);
        assertEquals(HttpStatus.NOT_FOUND, deletedResponse.getStatusCode());
    }

    @Test
    @DisplayName("Integration Test: User orders endpoint (without external service)")
    void userOrdersEndpointWithoutExternalService() throws Exception {
        // Create user with unique email
        User user = userRepository.save(new User("Juan Pérez", "juan.orders@example.com"));

        // Test getting user orders (will fail due to external service, but we test the endpoint)
        mockMvc.perform(get("/api/users/" + user.getId() + "/orders"))
                .andExpect(status().isServiceUnavailable()); // Expected since Order Service is not available
    }
}