package com.example.Web_sale_app.enums;

/**
 * Enum for payment methods
 */
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    ONLINE("Thanh toán trực tuyến");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
