package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.enums.ProductStatus;
import com.example.Web_sale_app.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * Controller for UC11 - Seller Product Management
 * Handles product CRUD operations for sellers including draft/publish workflow
 */
@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    private final CatalogService catalogService;

    // ===== PRODUCT LISTING =====

    /**
     * Lấy danh sách tất cả sản phẩm của seller
     */
    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getMyProducts(
            Authentication auth,
            Pageable pageable) {
        Long sellerId = getSellerId(auth);
        Page<ProductDTO> products = catalogService.getProductsByStatusForSeller(sellerId, null, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Lấy danh sách sản phẩm theo trạng thái
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductDTO>> getProductsByStatus(
            @PathVariable ProductStatus status,
            Authentication auth,
            Pageable pageable) {
        Long sellerId = getSellerId(auth);
        Page<ProductDTO> products = catalogService.getProductsByStatusForSeller(sellerId, status, pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Lấy chi tiết sản phẩm
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(
            @PathVariable Long id,
            Authentication auth) {
        // Sử dụng getProductDetailDTO và kiểm tra ownership trong service
        return catalogService.getProductDetailDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== UC11 - DRAFT/PUBLISH WORKFLOW =====

    /**
     * Tạo sản phẩm mới ở trạng thái nháp
     */
    @PostMapping("/draft")
    public ResponseEntity<ProductDTO> createProductDraft(
            @Valid @RequestBody ProductDTO productDTO,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            ProductDTO created = catalogService.createProductDraft(productDTO, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lưu nháp sản phẩm (cập nhật mà không xuất bản)
     */
    @PutMapping("/{id}/draft")
    public ResponseEntity<ProductDTO> saveProductDraft(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            ProductDTO updated = catalogService.saveProductDraft(id, productDTO, sellerId);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Xuất bản sản phẩm từ nháp
     */
    @PostMapping("/{id}/publish")
    public ResponseEntity<ProductDTO> publishProduct(
            @PathVariable Long id,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            ProductDTO published = catalogService.publishProduct(id, sellerId);
            return ResponseEntity.ok(published);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ===== PRODUCT STATUS MANAGEMENT =====

    /**
     * Ẩn sản phẩm tạm thời
     */
    @PostMapping("/{id}/hide")
    public ResponseEntity<Void> hideProduct(
            @PathVariable Long id,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            catalogService.hideProductTemporary(id, sellerId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Hiện lại sản phẩm đã ẩn
     */
    @PostMapping("/{id}/unhide")
    public ResponseEntity<Void> unhideProduct(
            @PathVariable Long id,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            catalogService.unhideProduct(id, sellerId);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Cập nhật trạng thái sản phẩm
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            ProductStatus status = ProductStatus.valueOf(request.get("status"));
            ProductDTO updated = catalogService.updateProductStatus(id, status, sellerId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", "Invalid status value")
                    .build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ===== TRADITIONAL CRUD OPERATIONS =====

    /**
     * Tạo sản phẩm (traditional way - sẽ redirect sang draft workflow)
     */
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            Authentication auth) {
        // Redirect to draft creation for consistency
        return createProductDraft(productDTO, auth);
    }

    /**
     * Cập nhật sản phẩm
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            ProductDTO updated = catalogService.updateProduct(id, productDTO, sellerId);
            return ResponseEntity.ok(updated);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Xóa sản phẩm
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            catalogService.deleteProduct(id, sellerId);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ===== CLONE FUNCTIONALITY =====

    /**
     * Clone sản phẩm từ sản phẩm khác
     */
    @PostMapping("/{sourceId}/clone")
    public ResponseEntity<ProductDTO> cloneProduct(
            @PathVariable Long sourceId,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            ProductDTO cloned = catalogService.cloneProduct(sourceId, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cloned);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Lấy thống kê sản phẩm của seller
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getProductStatistics(Authentication auth) {
        Long sellerId = getSellerId(auth);
        
        // Get counts by status
        long draftCount = catalogService.getProductsByStatusForSeller(sellerId, ProductStatus.DRAFT, Pageable.unpaged()).getTotalElements();
        long publishedCount = catalogService.getProductsByStatusForSeller(sellerId, ProductStatus.PUBLISHED, Pageable.unpaged()).getTotalElements();
        long hiddenCount = catalogService.getProductsByStatusForSeller(sellerId, ProductStatus.HIDDEN, Pageable.unpaged()).getTotalElements();
        
        Map<String, Object> stats = Map.of(
                "totalProducts", draftCount + publishedCount + hiddenCount,
                "draftProducts", draftCount,
                "publishedProducts", publishedCount,
                "hiddenProducts", hiddenCount
        );
        
        return ResponseEntity.ok(stats);
    }

    // ===== HELPER METHODS =====

    /**
     * Extract seller ID from authentication
     */
    private Long getSellerId(Authentication auth) {
        // Implementation depends on your authentication setup
        // This is a placeholder - adjust according to your User entity
        return Long.valueOf(auth.getName()); // Adjust this based on your auth implementation
    }
}
