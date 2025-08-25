package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Order;
import com.example.Web_sale_app.enums.OrderStatus;  // ✅ Thêm import này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Order> findByUser_IdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status, Pageable pageable);  // ✅ Bây giờ sẽ hoạt động

    Optional<Order> findByIdAndUser_Id(Long id, Long userId);

    // Guest tra cứu
    Optional<Order> findByIdAndRecipientEmailIgnoreCase(Long id, String email);
    Optional<Order> findByIdAndRecipientPhone(Long id, String phone);
}
