package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final CatalogService catalogService;

    // ===== PRODUCT CRUD OPERATIONS =====

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        ProductDTO created = catalogService.createProductAsAdmin(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        ProductDTO updated = catalogService.updateProductAsAdmin(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        catalogService.deleteProductAsAdmin(id);
        return ResponseEntity.noContent().build();
    }

    // ===== PRODUCT LISTING =====

    @GetMapping
    public Page<ProductDTO> listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String dir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        String sortField = switch (sort) { 
            case "name" -> "name"; 
            case "price" -> "price"; 
            default -> "createdAt"; 
        };
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(
            Math.max(page, 0), 
            Math.min(Math.max(size, 1), 100), 
            Sort.by(direction, sortField)
        );
        
        // Admin có thể lấy tất cả sản phẩm hoặc filter
        if (categoryId == null && search == null && minPrice == null && maxPrice == null) {
            return catalogService.listAllProductsForAdmin(pageable);
        }
        return catalogService.listProducts(categoryId, search, minPrice, maxPrice, pageable);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductDTO>> listAllActiveProducts() {
        return ResponseEntity.ok(catalogService.listAllActiveProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductDetail(@PathVariable Long id) {
        return catalogService.getProductDetailDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== PRODUCT STATUS MANAGEMENT =====

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<Void> toggleProductActiveStatus(@PathVariable Long id) {
        try {
            catalogService.toggleProductActiveStatus(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<Void> hideProduct(@PathVariable Long id) {
        try {
            catalogService.hideProductAsAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/unhide")
    public ResponseEntity<Void> unhideProduct(@PathVariable Long id) {
        try {
            catalogService.unhideProductAsAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== ADVANCED PRODUCT MANAGEMENT =====

    @PostMapping("/{id}/clone")
    public ResponseEntity<ProductDTO> cloneProduct(@PathVariable Long id) {
        try {
            ProductDTO cloned = catalogService.cloneProduct(id);
            return ResponseEntity.ok(cloned);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<ProductDTO>> getProductsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        try {
            com.example.Web_sale_app.enums.ProductStatus productStatus = 
                com.example.Web_sale_app.enums.ProductStatus.valueOf(status.toUpperCase());
            
            Pageable pageable = PageRequest.of(page, size);
            Page<ProductDTO> products = catalogService.getProductsByStatus(productStatus, pageable);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<ProductDTO> updateProductStatus(
            @PathVariable Long id, 
            @PathVariable String status) {
        try {
            com.example.Web_sale_app.enums.ProductStatus productStatus = 
                com.example.Web_sale_app.enums.ProductStatus.valueOf(status.toUpperCase());
            
            ProductDTO updated = catalogService.updateProductStatus(id, productStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
