package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.repository.CategoryRepository;
import com.example.Web_sale_app.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogController(CategoryRepository categoryRepository,
                             ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    // 1) Xem danh mục
    @GetMapping("/categories")
    public List<Category> listCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    // 2) Tìm kiếm & lọc & sắp xếp sản phẩm (có thể kèm categoryId)
    @GetMapping("/products")
    public Page<Product> listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String q,                 // từ khóa
            @RequestParam(required = false) BigDecimal minPrice,      // giá tối thiểu
            @RequestParam(required = false) BigDecimal maxPrice,      // giá tối đa
            @RequestParam(defaultValue = "createdAt") String sort,    // name | price | createdAt
            @RequestParam(defaultValue = "desc") String dir,          // asc | desc
            @RequestParam(defaultValue = "0") int page,               // 0-based
            @RequestParam(defaultValue = "12") int size               // page size
    ) {
        // Chuẩn hóa sort field
        String sortField = switch (sort) {
            case "name" -> "name";
            case "price" -> "price";
            default -> "createdAt";
        };
        Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(direction, sortField));

        // Specification động
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), like));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // NOTE: Nếu có cột trạng thái hiển thị (ví dụ: isActive / visible), thêm điều kiện ở đây
            // predicates.add(cb.isTrue(root.get("visible")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
    }

    // 3) Lấy sản phẩm theo danh mục (alias của /products?categoryId=)
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
        return listProducts(categoryId, q, minPrice, maxPrice, sort, dir, page, size);
    }

    // 4) Xem chi tiết sản phẩm
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductDetail(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
