package com.example.Web_sale_app.dto;

public record UpdateCartItemRequest(
        Integer quantity // >= 0; =0 sẽ coi như xóa
) {}
