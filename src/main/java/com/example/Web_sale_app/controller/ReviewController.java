package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.ReqDTO.ReqReviewDTO;
import com.example.Web_sale_app.entity.Review;
import com.example.Web_sale_app.entity.UserDetail;
import com.example.Web_sale_app.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody ReqReviewDTO req) {
        reviewService.addReview(req);
        return ResponseEntity.ok("Add review successfully!");
    }

    @GetMapping("/byProduct")
    public ResponseEntity<?> findByProductId(@RequestParam Long productId){
        return ResponseEntity.ok(reviewService.findByProductId(productId));
    }

    @GetMapping("/byUser")
    public ResponseEntity<?> findByUserId(@RequestParam Long userId){
        return ResponseEntity.ok(reviewService.findByUserId(userId));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(long productId, Authentication authentication){
        UserDetail user = (UserDetail) authentication.getPrincipal();

        reviewService.deleteReview(productId, user.getUser().getId());
        return ResponseEntity.ok("Delete review successfully!");
    }
}
