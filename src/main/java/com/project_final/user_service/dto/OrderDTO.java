package com.project_final.user_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {

    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;

    // Constructores
    public OrderDTO() {}

    public OrderDTO(Long id, Long productId, Integer quantity, BigDecimal totalPrice, LocalDateTime orderDate) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
