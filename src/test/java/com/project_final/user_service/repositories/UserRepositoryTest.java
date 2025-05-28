package com.project_final.user_service.repositories;

import com.project_final.user_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Unit Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User("Juan Pérez", "juan@example.com");
        testUser2 = new User("Ana García", "ana@example.com");

        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("juan@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Juan Pérez", found.get().getName());
        assertEquals("juan@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        // When
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // When & Then
        assertTrue(userRepository.existsByEmail("juan@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should find users by name containing")
    void shouldFindUsersByNameContaining() {
        // Given
        User testUser3 = new User("Juan Carlos", "juancarlos@example.com");
        entityManager.persistAndFlush(testUser3);

        // When
        List<User> found = userRepository.findByNameContaining("Juan");

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(u -> u.getName().equals("Juan Pérez")));
        assertTrue(found.stream().anyMatch(u -> u.getName().equals("Juan Carlos")));
    }

    @Test
    @DisplayName("Should return empty list when no names match")
    void shouldReturnEmptyListWhenNoNamesMatch() {
        // When
        List<User> found = userRepository.findByNameContaining("Pedro");

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should count all users")
    void shouldCountAllUsers() {
        // When
        Long count = userRepository.countAllUsers();

        // Then
        assertEquals(2L, count);
    }

    @Test
    @DisplayName("Should save user with timestamps")
    void shouldSaveUserWithTimestamps() {
        // Given
        User newUser = new User("Carlos López", "carlos@example.com");

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals(saved.getCreatedAt(), saved.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update user and modify updatedAt timestamp")
    void shouldUpdateUserAndModifyUpdatedAtTimestamp() throws InterruptedException {
        // Given
        User user = userRepository.findByEmail("juan@example.com").orElseThrow();

        // Small delay to ensure timestamp difference
        Thread.sleep(10);

        // When
        user.setName("Juan Carlos Pérez");
        User updated = userRepository.save(user);

        // Then
        assertEquals("Juan Carlos Pérez", updated.getName());
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));
    }

    @Test
    @DisplayName("Should maintain email uniqueness constraint")
    void shouldMaintainEmailUniquenessConstraint() {
        // Given
        User duplicateEmailUser = new User("Otro Usuario", "juan@example.com");

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateEmailUser);
        });
    }
}
