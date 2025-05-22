package com.project_final.user_service.repositories;

import com.project_final.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por email_
    Optional<User> findByEmail(String email);

    // Verificar si existe un usuario con ese email
    boolean existsByEmail(String email);

    // Buscar usuarios por nombre (contiene)
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    java.util.List<User> findByNameContaining(@Param("name") String name);

    // Contar usuarios activos
    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();
}
