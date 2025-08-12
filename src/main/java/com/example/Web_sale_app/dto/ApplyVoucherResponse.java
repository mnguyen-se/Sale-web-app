package com.example.Web_sale_app.dto;

import java.math.BigDecimal;

public record ApplyVoucherResponse(
        Long cartId,
        String code,
        boolean applied,       // áp thành công?
        String message,        // lý do nếu fail/thành công
        BigDecimal discount    // số tiền giảm (tạm tính)
) {}
