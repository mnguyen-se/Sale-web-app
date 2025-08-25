package com.example.Web_sale_app.dto.Req;

import com.example.Web_sale_app.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * DTO for checkout request with comprehensive validation
 */
public record CheckoutRequest(
        @Schema(description = "ID giỏ hàng", example = "1")
        @Min(value = 1, message = "Cart ID phải lớn hơn 0")
        Long cartId,
        
        @NotBlank(message = "Họ tên không được để trống")
        @Size(min = 2, max = 100, message = "Họ tên phải từ 2-100 ký tự")
        @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Họ tên chỉ được chứa chữ cái và khoảng trắng")
        @Schema(description = "Họ tên người nhận", example = "Nguyễn Văn A", required = true)
        String fullName,
        
        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ (VD: 0987654321 hoặc +84987654321)")
        @Schema(description = "Số điện thoại", example = "0987654321", required = true)
        String phone,
        
        @Email(message = "Email không hợp lệ")
        @Size(max = 100, message = "Email không được vượt quá 100 ký tự")
        @Schema(description = "Email liên hệ", example = "user@example.com")
        String email,
        
        @NotBlank(message = "Địa chỉ không được để trống")
        @Size(min = 10, max = 200, message = "Địa chỉ phải từ 10-200 ký tự")
        @Schema(description = "Địa chỉ chi tiết", example = "123 Đường Nguyễn Văn Linh", required = true)
        String address,
        
        @NotBlank(message = "Phường/Xã không được để trống")
        @Size(min = 2, max = 50, message = "Phường/Xã phải từ 2-50 ký tự")
        @Schema(description = "Phường/Xã", example = "Phường Tân Thuận Đông", required = true)
        String ward,
        
        @NotBlank(message = "Quận/Huyện không được để trống")
        @Size(min = 2, max = 50, message = "Quận/Huyện phải từ 2-50 ký tự")
        @Schema(description = "Quận/Huyện", example = "Quận 7", required = true)
        String district,
        
        @NotBlank(message = "Tỉnh/Thành phố không được để trống")
        @Size(min = 2, max = 50, message = "Tỉnh/Thành phố phải từ 2-50 ký tự")
        @Schema(description = "Tỉnh/Thành phố", example = "TP.Hồ Chí Minh", required = true)
        String province,
        
        @Size(max = 20, message = "Mã voucher không được vượt quá 20 ký tự")
        @Pattern(regexp = "^[A-Z0-9]*$", message = "Mã voucher chỉ được chứa chữ hoa và số")
        @Schema(description = "Mã voucher giảm giá (tùy chọn)", example = "COTHENULL")
        String voucher,
        
        @NotNull(message = "Phương thức thanh toán không được để trống")
        @Schema(
            description = "Phương thức thanh toán", 
            allowableValues = {"COD", "ONLINE"},
            required = true,
            example = "COD"
        )
        PaymentMethod paymentMethod
) {
    /**
     * Custom validation logic for business rules
     */
    public void validateBusinessRules() {
        // Email bắt buộc với thanh toán online
        if (paymentMethod == PaymentMethod.ONLINE && (email == null || email.isBlank())) {
            throw new IllegalArgumentException("Email là bắt buộc khi thanh toán trực tuyến");
        }
        
        // Validate voucher format nếu có
        if (voucher != null && !voucher.isBlank() && voucher.length() < 3) {
            throw new IllegalArgumentException("Mã voucher phải có ít nhất 3 ký tự");
        }
    }
   
}

