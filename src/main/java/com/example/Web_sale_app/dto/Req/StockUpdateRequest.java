package com.example.Web_sale_app.dto.Req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockUpdateRequest(
    @NotNull(message = "Product ID không được null")
    Long productId,
    
    @NotNull(message = "Số lượng tồn kho không được null")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    Integer newStock
) {}
