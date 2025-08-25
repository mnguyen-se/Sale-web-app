package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.CartItemDTO;
import com.example.Web_sale_app.dto.Req.AddToCartRequest;
import com.example.Web_sale_app.dto.Res.AddToCartResponse;
import com.example.Web_sale_app.entity.Cart;
import com.example.Web_sale_app.entity.CartItem;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.repository.CartItemRepository;
import com.example.Web_sale_app.repository.CartRepository;
import com.example.Web_sale_app.repository.ProductRepository;
import com.example.Web_sale_app.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private static final int MAX_PER_ITEM = 10;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public AddToCartResponse addItem(AddToCartRequest req) {
        if (req.productId() == null || req.quantity() == null || req.quantity() <= 0) {
            throw new IllegalArgumentException("productId và quantity (>0) là bắt buộc");
        }

        // 1) Tìm/ tạo giỏ
        Cart cart = (req.cartId() != null)
                ? cartRepository.findById(req.cartId())
                .orElseGet(() -> cartRepository.save(new Cart()))
                : cartRepository.save(new Cart());

        // 2) Kiểm tra sản phẩm
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        int requested = req.quantity();
        int available = product.getStock() != null ? product.getStock() : 0;
        int capByMax = Math.min(requested, MAX_PER_ITEM);
        int finalQtyToAdd;

        boolean adjusted = false;
        String message = null;

        if (available <= 0) {
            // Hết hàng
            throw new IllegalStateException("Hết hàng");
        }

        // 3) Upsert item: nếu đã có item trong giỏ, cộng dồn
        CartItem item = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId())
                .orElse(null);

        int currentInCart = (item != null) ? item.getQuantity() : 0;

        // Số lượng tối đa được phép sau khi cộng dồn (không vượt MAX_PER_ITEM và không vượt tồn)
        int maxAllowed = Math.min(MAX_PER_ITEM, available);
        int desiredTotal = currentInCart + requested;
        int finalTotal = Math.min(desiredTotal, maxAllowed);

        if (finalTotal <= currentInCart) {
            // Nghĩa là không thể tăng thêm
            adjusted = true;
            message = "Vượt định mức hoặc vượt tồn. Số lượng giữ nguyên: " + currentInCart;
            finalQtyToAdd = 0;
        } else {
            finalQtyToAdd = finalTotal - currentInCart;
            if (finalTotal < desiredTotal) {
                adjusted = true;
                message = "Đã điều chỉnh theo giới hạn: tối đa " + maxAllowed + " hoặc theo tồn " + available;
            }
        }

        if (finalQtyToAdd > 0) {
            if (item == null) {
                item = new CartItem();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity(finalQtyToAdd);
            } else {
                item.setQuantity(item.getQuantity() + finalQtyToAdd);
            }
            cartItemRepository.save(item);
        }

        // 4) Trả về giỏ hiện tại
        List<CartItemDTO> items = new ArrayList<>();
        // Nên load lại từ DB để phản ánh số liệu mới nhất
        cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), product.getId())
                .ifPresent(updated -> {
                    var unitPrice = updated.getProduct().getPrice();
                    var lineTotal = unitPrice.multiply(BigDecimal.valueOf(updated.getQuantity()));
                    items.add(new CartItemDTO(
                            updated.getId(),
                            updated.getProduct().getId(),
                            updated.getProduct().getName(),
                            updated.getQuantity(),
                            unitPrice,
                            lineTotal
                    ));
                });

        return new AddToCartResponse(
                cart.getId(),
                adjusted,
                message,
                items
        );
    }
}
