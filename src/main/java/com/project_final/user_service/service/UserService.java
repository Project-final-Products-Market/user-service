package com.project_final.user_service.service;

import com.project_final.user_service.model.User;
import com.project_final.user_service.dto.OrderDTO;
import com.project_final.user_service.repositories.UserRepository;
import com.project_final.user_service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    // URL del Order Service (configurar en application.properties)
    private final String ORDER_SERVICE_URL = "http://order-service/api/orders";

    // Crear usuario
    public User createUser(User user) {
        // Validar datos básicos
        validateUserData(user);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw UserAlreadyExistsException.forEmail(user.getEmail());
        }
        return userRepository.save(user);
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener usuario por ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Obtener usuario por email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Actualizar usuario
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Validar nuevos datos
        validateUserData(userDetails);

        // Verificar si el nuevo email ya existe (si es diferente al actual)
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw UserAlreadyExistsException.forEmail(userDetails.getEmail());
        }

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user);
    }

    // Eliminar usuario
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);
    }

    // Buscar usuarios por nombre
    public List<User> searchUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserValidationException("name", "El nombre no puede estar vacío");
        }
        return userRepository.findByNameContaining(name);
    }

    // Obtener órdenes de un usuario (comunicación con Order Service)
    public List<OrderDTO> getUserOrders(Long userId) {
        // Verificar que el usuario existe
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        try {
            String url = ORDER_SERVICE_URL + "/user/" + userId;
            OrderDTO[] orders = restTemplate.getForObject(url, OrderDTO[].class);
            return orders != null ? List.of(orders) : List.of();
        } catch (RestClientException e) {
            throw new OrderServiceException(userId, e);
        }
    }

    // Contar total de usuarios
    public Long getTotalUsers() {
        return userRepository.countAllUsers();
    }

    // Método privado para validar datos del usuario
    private void validateUserData(User user) {
        if (user == null) {
            throw new UserValidationException("Los datos del usuario no pueden ser nulos");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new UserValidationException("name", "El nombre es obligatorio");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new UserValidationException("email", "El email es obligatorio");
        }

        // Validación básica de email
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UserValidationException("email", "El formato del email no es válido");
        }

        // Validación de longitud del nombre
        if (user.getName().length() > 100) {
            throw new UserValidationException("name", "El nombre no puede tener más de 100 caracteres");
        }
    }
}