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
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final CatalogService catalogService;

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        ProductDTO created = catalogService.createProductAsAdmin(dto); // Sử dụng method admin
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        ProductDTO updated = catalogService.updateProductAsAdmin(id, dto); // Sử dụng method admin
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        catalogService.deleteProductAsAdmin(id); // Sử dụng method admin
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
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
        String sortField = switch (sort) { case "name" -> "name"; case "price" -> "price"; default -> "createdAt"; };
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(direction, sortField));
        
        // Admin có thể lấy tất cả sản phẩm hoặc filter
        if (categoryId == null && search == null && minPrice == null && maxPrice == null) {
            return catalogService.listAllProductsForAdmin(pageable);
        }
        return catalogService.listProducts(categoryId, search, minPrice, maxPrice, pageable);
    }

    @PatchMapping("/toggle-active/{id}")
    public ResponseEntity<Void> toggleProductActiveStatus(@PathVariable Long id) {
        catalogService.toggleProductActiveStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/hidden/{id}")
    public ResponseEntity<Void> hiddenProduct(@PathVariable Long id) {
        catalogService.hideProduct(id, null);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/unhidden/{id}")
    public ResponseEntity<Void> unhiddenProduct(@PathVariable Long id) {
        catalogService.unhideProduct(id, null);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active")
    public ResponseEntity<java.util.List<ProductDTO>> listAllActiveProducts() {
        return ResponseEntity.ok(catalogService.listAllActiveProducts());
    }

   
}
