package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.dto.StockImportResult;
import com.example.Web_sale_app.dto.StockReport;
import com.example.Web_sale_app.dto.Req.StockUpdateRequest;
import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.enums.ProductStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for catalog browsing and product management operations
 */
public interface CatalogService {
    // ===== CATEGORY OPERATIONS =====
    List<Category> listCategories();
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    Optional<Category> getCategoryById(Long id);
    
    // ===== PRODUCT SEARCH/VIEW OPERATIONS =====
    Page<ProductDTO> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<ProductDTO> listProductsForBuyer(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Optional<ProductDTO> getProductDetailDTO(Long id);
    List<ProductDTO> listAllActiveProducts();
    
    // ===== PRODUCT MANAGEMENT (Generic - no seller distinction) =====
    ProductDTO createProduct(ProductDTO dto);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    void deleteProduct(Long id);
    void hideProduct(Long id);
    void unhideProduct(Long id);
    
    // ===== ADMIN PRODUCT MANAGEMENT =====
    ProductDTO createProductAsAdmin(ProductDTO dto);
    ProductDTO updateProductAsAdmin(Long id, ProductDTO dto);
    void deleteProductAsAdmin(Long id);
    void hideProductAsAdmin(Long id);
    void unhideProductAsAdmin(Long id);
    Page<ProductDTO> listAllProductsForAdmin(Pageable pageable);
    void toggleProductActiveStatus(Long id);
    
    // ===== UC11 - PRODUCT WORKFLOW (Generic) =====
    ProductDTO createProductDraft(ProductDTO dto);
    ProductDTO saveProductDraft(Long id, ProductDTO dto);
    ProductDTO publishProduct(Long id);
    void hideProductTemporary(Long id);
    ProductDTO cloneProduct(Long sourceProductId);
    Page<ProductDTO> getProductsByStatus(ProductStatus status, Pageable pageable);
    ProductDTO updateProductStatus(Long id, ProductStatus status);
    
    // ===== UC12 - STOCK MANAGEMENT (Generic) =====
    
    /**
     * UC12 - Cập nhật tồn kho đơn lẻ
     */
    ProductDTO updateProductStock(Long id, Integer newStock);
    
    /**
     * UC12 - Cập nhật tồn kho hàng loạt
     */
    List<ProductDTO> updateBatchStock(List<StockUpdateRequest> stockUpdates);
    
    /**
     * UC12 - Cấu hình cảnh báo tồn kho thấp
     */
    void configureLowStockAlert(Long productId, Integer threshold);
    
    /**
     * UC12 - Lấy danh sách sản phẩm tồn kho thấp
     */
    Page<ProductDTO> getLowStockProducts(Pageable pageable);
    
    /**
     * UC12 - Lấy báo cáo tồn kho
     */
    StockReport getStockReport();
    
    // ===== FUTURE FEATURES (DEFAULT IMPLEMENTATIONS) =====
    default Page<ProductDTO> searchProductsByKeywords(String[] keywords, Pageable pageable) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    default List<ProductDTO> getRelatedProducts(Long productId, int limit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    default List<ProductDTO> getNewestProducts(int limit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    default List<ProductDTO> getFeaturedProducts(int limit) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    // ===== ADMIN-ONLY FEATURES (if needed in future) =====
    default StockImportResult importStockFromCsv(String csvContent) {
        throw new UnsupportedOperationException("CSV import not implemented yet");
    }
}
