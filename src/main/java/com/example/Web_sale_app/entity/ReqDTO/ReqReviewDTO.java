package com.example.Web_sale_app.entity.ReqDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqReviewDTO {
    private Long productId;
    private Long userId;
    private String comment;
    private Integer rating;
}
