package com.example.Web_sale_app.entity;

import com.example.Web_sale_app.enums.ReturnStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "order_returns")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReturn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long customerId;
    private String reason;
    private List<String> evidenceUrls;
    @Enumerated(EnumType.STRING)
    private ReturnStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
