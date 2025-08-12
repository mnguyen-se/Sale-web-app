package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CatalogService {
    List<Category> listCategories();
    Page<Product> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Optional<Product> getProductDetail(Long id);
}
