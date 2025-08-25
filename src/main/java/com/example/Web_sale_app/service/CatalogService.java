package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.entity.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for catalog browsing and product management operations
 */
public interface CatalogService {
    // Category operations
    List<Category> listCategories();
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    Optional<Category> getCategoryById(Long id);
    
    // Product search/view operations
    Page<ProductDTO> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<ProductDTO> listProductsForBuyer(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Optional<ProductDTO> getProductDetailDTO(Long id);
    List<ProductDTO> listAllActiveProducts();
    
    // Seller product management
    ProductDTO createProduct(ProductDTO dto, Long sellerId);
    ProductDTO updateProduct(Long id, ProductDTO dto, Long sellerId);
    void deleteProduct(Long id, Long sellerId);
    void hideProduct(Long id, Long sellerId);
    void unhideProduct(Long id, Long sellerId);
    
    // Admin product management
    ProductDTO createProductAsAdmin(ProductDTO dto);
    ProductDTO updateProductAsAdmin(Long id, ProductDTO dto);
    void deleteProductAsAdmin(Long id);
    void hideProductAsAdmin(Long id);
    void unhideProductAsAdmin(Long id);
    Page<ProductDTO> listAllProductsForAdmin(Pageable pageable);
    void toggleProductActiveStatus(Long id);
    
    ProductDTO createProductDraft(ProductDTO dto, Long sellerId);
    

    ProductDTO saveProductDraft(Long id, ProductDTO dto, Long sellerId);
    

    ProductDTO publishProduct(Long id, Long sellerId);
  
    void hideProductTemporary(Long id, Long sellerId);
    
  
    ProductDTO cloneProduct(Long sourceProductId, Long sellerId);
    
  
    Page<ProductDTO> getProductsByStatusForSeller(Long sellerId, ProductStatus status, Pageable pageable);
    
  
    ProductDTO updateProductStatus(Long id, ProductStatus status, Long sellerId);
    
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
}
