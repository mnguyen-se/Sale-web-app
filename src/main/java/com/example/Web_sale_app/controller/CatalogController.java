package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.entity.Category;
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

    // 2) Tìm kiếm & lọc & sắp xếp sản phẩm (trả DTO)
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
        return catalogService.listProductsForBuyer(categoryId, search, minPrice, maxPrice, pageable);
    }


    // 4) Chi tiết sản phẩm (nên trả DTO)
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProductDetail(@PathVariable Long id) {
        return catalogService.getProductDetailDTO(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 5) Danh sách tất cả sản phẩm đang active (không phân trang)
    @GetMapping("/products/active")
    public ResponseEntity<List<ProductDTO>> listAllActiveProducts() {
        return ResponseEntity.ok(catalogService.listAllActiveProducts());
    }
}
