package com.project_final.user_service.controller;

import com.project_final.user_service.model.User;
import com.project_final.user_service.dto.OrderDTO;
import com.project_final.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Crear usuario
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        logger.info("Petición para crear usuario: {}", user.getName());

        try {
            User createdUser = userService.createUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario creado correctamente");
            response.put("user", createdUser);
            response.put("userId", createdUser.getId());
            response.put("name", createdUser.getName());
            response.put("email", createdUser.getEmail());
            response.put("timestamp", LocalDateTime.now());

            logger.info("Usuario creado exitosamente con ID: {}", createdUser.getId());
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            logger.error("Error creando usuario: {}", e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al crear usuario");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Obtener usuario por email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        logger.info("Petición para actualizar usuario: {}", id);

        try {
            User updatedUser = userService.updateUser(id, userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario actualizado correctamente");
            response.put("user", updatedUser);
            response.put("userId", updatedUser.getId());
            response.put("name", updatedUser.getName());
            response.put("email", updatedUser.getEmail());
            response.put("timestamp", LocalDateTime.now());

            logger.info("Usuario {} actualizado exitosamente", id);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            logger.error("Error actualizando usuario {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar usuario");
            errorResponse.put("userId", id);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Petición para eliminar usuario: {}", id);

        try {
            // Obtener información del usuario antes de eliminarlo
            Optional<User> userToDelete = userService.getUserById(id);

            if (userToDelete.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteUser(id);

            logger.info("Usuario {} eliminado exitosamente", id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            logger.error("Error eliminando usuario {}: {}", id, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al eliminar usuario");
            errorResponse.put("userId", id);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Buscar usuarios por nombre
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Obtener órdenes de un usuario
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long id) {
        try {
            List<OrderDTO> orders = userService.getUserOrders(id);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    // Obtener estadísticas
    @GetMapping("/stats/total")
    public ResponseEntity<Long> getTotalUsers() {
        Long total = userService.getTotalUsers();
        return new ResponseEntity<>(total, HttpStatus.OK);
    }
}