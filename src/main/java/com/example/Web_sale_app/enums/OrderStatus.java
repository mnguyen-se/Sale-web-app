package com.example.Web_sale_app.enums;

public enum OrderStatus {
    PENDING,           // tạo đơn xong (COD) hoặc chờ thanh toán (ONLINE)
    PAID,              // thanh toán thành công (ONLINE)
    PROCESSING,        // cửa hàng đang xử lý
    SHIPPED,           // đã giao cho đơn vị vận chuyển
    COMPLETED,         // giao thành công
    CANCELLED          // đã hủy
}