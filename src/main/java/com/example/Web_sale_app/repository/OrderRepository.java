package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser_Id(Long userId, Pageable pageable);
    Page<Order> findByStatus(String status, Pageable pageable);
}
