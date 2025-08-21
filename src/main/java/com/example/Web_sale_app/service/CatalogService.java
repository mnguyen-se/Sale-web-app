package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public interface CatalogService {
    List<Category> listCategories();
    Page<ProductDTO> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Optional<ProductDTO> getProductDetailDTO(Long id);
    ProductDTO createProduct(ProductDTO dto, Long sellerId);
    ProductDTO updateProduct(Long id, ProductDTO dto, Long sellerId);
    void deleteProduct(Long id, Long sellerId);
    Optional<ProductDTO> getProductByIdForEdit(Long id, Long sellerId);
}
