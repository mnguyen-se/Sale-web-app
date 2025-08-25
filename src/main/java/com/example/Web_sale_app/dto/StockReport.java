package com.example.Web_sale_app.dto;

import java.math.BigDecimal;

public record StockReport(
    int totalProducts,
    int inStockProducts,
    int outOfStockProducts,
    int lowStockProducts,
    BigDecimal totalStockValue
) {}
