package com.jonathan.carrasco.orderservice.repository;

import com.jonathan.carrasco.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRespository extends JpaRepository<Order, Long> {
}
