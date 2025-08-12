package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findTopByUser_IdOrderByIdDesc(Long userId); // giỏ hiện tại của user
}
