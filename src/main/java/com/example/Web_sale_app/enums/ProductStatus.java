package com.example.Web_sale_app.enums;

public enum ProductStatus {
    DRAFT("Nháp"),
    PUBLISHED("Đã hiển thị"), 
    HIDDEN("Ẩn"),
    OUT_OF_STOCK("Hết hàng");

    private final String displayName;

    ProductStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
