package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Category Management
 */
@Tag(name = "Categories", description = "API quản lý danh mục sản phẩm")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CatalogService catalogService;

    /**
     * Lấy danh sách tất cả categories (public)
     */
    @Operation(summary = "Lấy danh sách danh mục")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = catalogService.listCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Lấy chi tiết category theo ID
     */
    @Operation(summary = "Lấy chi tiết danh mục")
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return catalogService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Tạo category mới (Admin only)
     */
    @Operation(summary = "Tạo danh mục mới")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category created = catalogService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Cập nhật category (Admin only)
     */
    @Operation(summary = "Cập nhật danh mục")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        try {
            Category updated = catalogService.updateCategory(id, category);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Xóa category (Admin only)
     */
    @Operation(summary = "Xóa danh mục")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            catalogService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
