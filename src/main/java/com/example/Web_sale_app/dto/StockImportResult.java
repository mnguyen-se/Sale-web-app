package com.example.Web_sale_app.dto;

import java.util.List;

public record StockImportResult(
    int totalRows,
    int successCount,
    int errorCount,
    List<ProductDTO> successProducts,
    List<String> errors
) {}
