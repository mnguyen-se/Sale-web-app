package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.*;
import com.example.Web_sale_app.dto.Req.StockUpdateRequest;
import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CatalogService {
    
    // ===== CATEGORY MANAGEMENT =====
    List<Category> listCategories();
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    Optional<Category> getCategoryById(Long id);
    
    // ===== PRODUCT BASIC OPERATIONS =====
    Page<ProductDTO> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<ProductDTO> listProductsForBuyer(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Optional<ProductDTO> getProductDetailDTO(Long id);
    List<ProductDTO> listAllActiveProducts();
    
    // ===== SELLER PRODUCT MANAGEMENT =====
    ProductDTO createProduct(ProductDTO dto, Long sellerId);
    ProductDTO updateProduct(Long id, ProductDTO dto, Long sellerId);
    void deleteProduct(Long id, Long sellerId);
    void hideProduct(Long id, Long sellerId);
    
    // ===== ADMIN PRODUCT MANAGEMENT =====
    ProductDTO createProductAsAdmin(ProductDTO dto);
    ProductDTO updateProductAsAdmin(Long id, ProductDTO dto);
    void deleteProductAsAdmin(Long id);
    void toggleProductActiveStatus(Long id);
    void hideProductAsAdmin(Long id);
    void unhideProductAsAdmin(Long id);
    Page<ProductDTO> listAllProductsForAdmin(Pageable pageable);
    
    // ===== UC11 - SELLER PRODUCT WORKFLOW =====
    ProductDTO createProductDraft(ProductDTO dto, Long sellerId);
    ProductDTO saveProductDraft(Long id, ProductDTO dto, Long sellerId);
    ProductDTO publishProduct(Long id, Long sellerId);
    void hideProductTemporary(Long id, Long sellerId);
    void unhideProduct(Long id, Long sellerId);
    ProductDTO cloneProduct(Long sourceProductId, Long sellerId);
    Page<ProductDTO> getProductsByStatusForSeller(Long sellerId, ProductStatus status, Pageable pageable);
    ProductDTO updateProductStatus(Long id, ProductStatus status, Long sellerId);
    
    // ===== UC12 - STOCK MANAGEMENT =====
    ProductDTO updateProductStock(Long id, Integer newStock, Long sellerId);
    List<ProductDTO> updateBatchStock(List<StockUpdateRequest> stockUpdates, Long sellerId);  // ✅ Thêm method này
    StockImportResult importStockFromCsv(String csvContent, Long sellerId);                    // ✅ Thêm method này
    void configureLowStockAlert(Long productId, Integer threshold, Long sellerId);             // ✅ Thêm method này
    Page<ProductDTO> getLowStockProducts(Long sellerId, Pageable pageable);                    // ✅ Thêm method này
    StockReport getStockReport(Long sellerId);                                                 // ✅ Thêm method này
}
