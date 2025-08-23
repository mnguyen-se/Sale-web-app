package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.ReqDTO.ReqReviewDTO;
import com.example.Web_sale_app.entity.Review;

import java.util.List;

public interface ReviewService {
    void addReview(ReqReviewDTO req);
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    void deleteReview(Long reviewId, Long userId);
}
