package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.entity.ReqDTO.ReqReviewDTO;
import com.example.Web_sale_app.entity.Review;
import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.repository.ProductRepository;
import com.example.Web_sale_app.repository.ReviewRepository;
import com.example.Web_sale_app.repository.UserRepository;
import com.example.Web_sale_app.service.ReviewService;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void addReview(ReqReviewDTO req) {
        boolean exists = reviewRepository.existsByUser_IdAndProduct_Id(req.getUserId(), req.getProductId());
        if(exists == false){
            User user = userRepository.findById(req.getUserId()).get();
            Product product = productRepository.findById(req.getProductId()).get();
            Review review = new Review();
            review.setComment(req.getComment());
            review.setRating(req.getRating());
            review.setUser(user);
            review.setProduct(product);
            reviewRepository.save(review);
        }
    }

    @Override
    public List<Review> findByProductId(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUser_Id(userId);
    }

    @Transactional
    @Override
    public void deleteReview(Long productId, Long userId) {
        reviewRepository.deleteByProduct_IdAndUser_Id(productId, userId);
    }
}
