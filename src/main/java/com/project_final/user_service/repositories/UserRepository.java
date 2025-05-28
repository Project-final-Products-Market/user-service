package com.project_final.user_service.repositories;

import com.project_final.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar usuario por email
    Optional<User> findByEmail(String email);

    // Verificar si existe un usuario con ese email
    boolean existsByEmail(String email);

    // Buscar usuarios por nombre (contiene el texto)
    List<User> findByNameContaining(String name);

    // Buscar usuarios por nombre exacto
    List<User> findByName(String name);

    // Contar todos los usuarios (query personalizada)
    @Query("SELECT COUNT(u) FROM User u")
    Long countAllUsers();

    // También puedes usar el método por defecto:
    // default: long count();
}
