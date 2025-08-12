package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProduct_Id(Long productId);
    List<Review> findByUser_Id(Long userId);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId); // 1 user 1 review/sản phẩm (nếu muốn)
}
