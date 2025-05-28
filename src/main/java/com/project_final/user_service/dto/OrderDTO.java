package com.project_final.user_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String description;
    private Integer quantity;

    // Constructor vacío
    public OrderDTO() {
    }

    // Constructor que coincida con lo que usan los tests
    // new OrderDTO(long, long, int, BigDecimal, LocalDateTime)
    public OrderDTO(Long id, Long userId, Integer statusCode, BigDecimal totalAmount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.status = convertStatusCodeToString(statusCode);
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // Constructor alternativo con status como String
    public OrderDTO(Long id, Long userId, String status, BigDecimal totalAmount, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // Constructor completo
    public OrderDTO(Long id, Long userId, String status, BigDecimal totalAmount,
                    LocalDateTime createdAt, LocalDateTime updatedAt, String description, Integer quantity) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.quantity = quantity;
    }

    // Método helper para convertir código de status a string
    private String convertStatusCodeToString(Integer statusCode) {
        if (statusCode == null) return "UNKNOWN";
        return switch (statusCode) {
            case 1 -> "PENDING";
            case 2 -> "CONFIRMED";
            case 3 -> "SHIPPED";
            case 4 -> "DELIVERED";
            case 5 -> "CANCELLED";
            default -> "UNKNOWN";
        };
    }

    // ==================== GETTERS Y SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
