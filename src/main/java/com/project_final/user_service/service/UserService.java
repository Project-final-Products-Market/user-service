package com.project_final.user_service.service;

import com.project_final.user_service.model.User;
import com.project_final.user_service.dto.OrderDTO;
import com.project_final.user_service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());

        return userRepository.save(user);
    }

    // Eliminar usuario
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        userRepository.delete(user);
    }

    // Buscar usuarios por nombre
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContaining(name);
    }

    // Obtener órdenes de un usuario (comunicación con Order Service)
    public List<OrderDTO> getUserOrders(Long userId) {
        try {
            String url = ORDER_SERVICE_URL + "/user/" + userId;
            OrderDTO[] orders = restTemplate.getForObject(url, OrderDTO[].class);
            return orders != null ? List.of(orders) : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener órdenes del usuario: " + e.getMessage());
        }
    }

    // Contar total de usuarios
    public Long getTotalUsers() {
        return userRepository.countAllUsers();
    }
}
