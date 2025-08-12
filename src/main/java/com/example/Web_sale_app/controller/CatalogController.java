package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.service.CatalogService;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // 1) Xem danh mục
    @GetMapping("/categories")
    public List<Category> listCategories() {
        return catalogService.listCategories();
    }

    // 2) Tìm kiếm & lọc & sắp xếp sản phẩm
    @GetMapping("/products")
    public Page<Product> listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,
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
        return catalogService.listProducts(categoryId, q, minPrice, maxPrice, pageable);
    }

    // 3) Alias theo danh mục
    @GetMapping("/categories/{categoryId}/products")
    public Page<Product> listProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String q,
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
        return catalogService.listProducts(categoryId, q, minPrice, maxPrice, pageable);
    }

    // 4) Chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductDetail(@PathVariable Long id) {
        return catalogService.getProductDetail(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
