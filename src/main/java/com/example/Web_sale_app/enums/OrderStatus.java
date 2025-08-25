package com.example.Web_sale_app.enums;

/**
 * Enum for order status lifecycle
 */
public enum OrderStatus {
    PENDING("Chờ xử lý"),           
    PAID("Đã thanh toán"),          
    PROCESSING("Đang xử lý"),       
    SHIPPED("Đã giao vận"),         
    COMPLETED("Hoàn thành"),        
    CANCELLED("Đã hủy");           

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}