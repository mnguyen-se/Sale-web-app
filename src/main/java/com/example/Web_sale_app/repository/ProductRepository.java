package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    // ===== CATEGORY QUERIES =====
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    
    // ===== SEARCH QUERIES =====
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name, Pageable pageable);
    
    // ===== ACTIVE STATUS QUERIES =====
    List<Product> findAllByIsActiveTrue(Sort sort);
    Page<Product> findAllByIsActive(boolean isActive, Pageable pageable);
    

    
    // ===== ADDITIONAL USEFUL QUERIES =====
    long countByIsActiveTrue();
    boolean existsByNameIgnoreCase(String name);

    //=====
    boolean existsByNameAndCategoryIdAndManufacturer(String name, Long categoryId, String manufacturer);
}
