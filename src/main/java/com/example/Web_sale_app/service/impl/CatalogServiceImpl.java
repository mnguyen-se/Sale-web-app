package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.dto.StockImportResult;
import com.example.Web_sale_app.dto.StockReport;
import com.example.Web_sale_app.dto.Req.StockUpdateRequest;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.enums.ProductStatus;
import com.example.Web_sale_app.repository.CategoryRepository;
import com.example.Web_sale_app.repository.ProductRepository;
import com.example.Web_sale_app.repository.CartItemRepository;
import com.example.Web_sale_app.repository.OrderItemRepository;
import com.example.Web_sale_app.repository.ReviewRepository;
import com.example.Web_sale_app.service.CatalogService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    public CatalogServiceImpl(CategoryRepository categoryRepository, 
                             ProductRepository productRepository,
                             CartItemRepository cartItemRepository,
                             OrderItemRepository orderItemRepository,
                             ReviewRepository reviewRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<com.example.Web_sale_app.entity.Category> listCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    private Page<ProductDTO> listProductsInternal(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable, Boolean isActive) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("name")), like),
                                cb.like(cb.lower(root.get("manufacturer")), like)
                        )
                );
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> products = productRepository.findAll(spec, pageable);

        return products.map(p -> new ProductDTO(
                p.getId(),
                p.getName(),
                p.getImageUrl(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getManufacturer(),
                p.getCategory() != null ? p.getCategory().getName() : null
        ));
    }

    @Override
    public Page<ProductDTO> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return listProductsInternal(categoryId, q, minPrice, maxPrice, pageable, null);
    }

    @Override
    public Page<ProductDTO> listProductsForBuyer(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return listProductsInternal(categoryId, q, minPrice, maxPrice, pageable, true);
    }

    @Override
    public Optional<ProductDTO> getProductDetailDTO(Long id) {
        return productRepository.findById(id).map(this::mapToDTO);
    }
    
    private ProductDTO mapToDTO(Product p) {
        return new ProductDTO(
                p.getId(),
                p.getName(),
                p.getImageUrl(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getManufacturer(),
                p.getCategory() != null ? p.getCategory().getName() : null
        );
    }

    // Loại bỏ sellerId parameter vì không còn seller field
    private Product mapDtoToProduct(ProductDTO dto, boolean isAdmin) {
        Product p = new Product();
        p.setName(dto.name());
        p.setImageUrl(dto.imageUrl());
        p.setManufacturer(dto.manufacturer());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setActive(isAdmin); // Admin tạo mặc định active

        if (dto.categoryName() != null) {
            categoryRepository.findByNameIgnoreCase(dto.categoryName())
                    .ifPresent(p::setCategory);
        }

        return p;
    }

    private void updateProductFromDto(Product p, ProductDTO dto) {
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setImageUrl(dto.imageUrl());
        p.setManufacturer(dto.manufacturer());
        if (dto.categoryName() != null) {
            categoryRepository.findByNameIgnoreCase(dto.categoryName())
                    .ifPresent(p::setCategory);
        }
    }

    // CREATE - Loại bỏ sellerId parameters
    @Override
    public ProductDTO createProduct(ProductDTO dto) {
        Product p = mapDtoToProduct(dto, false);
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }

    @Override
    public ProductDTO createProductAsAdmin(ProductDTO dto) {
        Product p = mapDtoToProduct(dto, true);
        p.setActive(true);
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }

    // UPDATE - Loại bỏ seller authorization checks
    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        updateProductFromDto(p, dto);
        return mapToDTO(productRepository.save(p));
    }

    @Override
    public ProductDTO updateProductAsAdmin(Long id, ProductDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        updateProductFromDto(p, dto);
        return mapToDTO(productRepository.save(p));
    }

    // DELETE - Loại bỏ seller authorization checks
    @Override
    public void deleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(p);
    }

    @Override
    public void deleteProductAsAdmin(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(p);
    }

    // HIDE/UNHIDE - Loại bỏ seller authorization checks
    @Override
    public void hideProduct(Long id) {
        hideProductTemporary(id);
    }

    private void updateProductActiveStatus(Long id, boolean isActive) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        p.setActive(isActive);
        productRepository.save(p);
    }

    @Override
    public List<ProductDTO> listAllActiveProducts() {
        List<Product> products = productRepository.findAllByIsActiveTrue(Sort.by(Sort.Direction.ASC, "name"));
        return products.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public Page<ProductDTO> listAllProductsForAdmin(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToDTO);
    }

    @Override
    public void toggleProductActiveStatus(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        p.setActive(!p.isActive());
        productRepository.save(p);
    }

    @Override
    public void hideProductAsAdmin(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Kiểm tra xem sản phẩm có đang được sử dụng không
        if (isProductInUse(id)) {
            // Nếu đang được sử dụng, chỉ ẩn không xóa dữ liệu liên quan
            product.setActive(false);
            product.setStatus(ProductStatus.HIDDEN);
        } else {
            // Nếu không được sử dụng, có thể cleanup
            cleanupProductRelatedData(id);
            product.setActive(false);
            product.setStatus(ProductStatus.HIDDEN);
        }
        
        product.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(product);
    }

    @Override
    public void unhideProductAsAdmin(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Kiểm tra tình trạng sản phẩm trước khi unhide
        if (product.getStock() > 0) {
            product.setActive(true);
            product.setStatus(ProductStatus.PUBLISHED);
        } else {
            // Nếu hết hàng, chỉ cho phép unhide nhưng không active
            product.setActive(false);
            product.setStatus(ProductStatus.OUT_OF_STOCK);
        }
        
        product.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(product);
    }

    /**
     * Kiểm tra xem sản phẩm có đang được sử dụng trong hệ thống không
     */
    private boolean isProductInUse(Long productId) {
        // Kiểm tra trong CartItem
        if (cartItemRepository.existsByProductId(productId)) {
            return true;
        }
        
        // Kiểm tra trong OrderItem
        if (orderItemRepository.existsByProductId(productId)) {
            return true;
        }
        
        // Kiểm tra trong Review
        if (reviewRepository.existsByProductId(productId)) {
            return true;
        }
        
        return false;
    }

    /**
     * Cleanup dữ liệu liên quan đến sản phẩm (chỉ khi cần thiết)
     */
    private void cleanupProductRelatedData(Long productId) {
        try {
            // Chỉ cleanup các dữ liệu tạm thời như cart items
            // KHÔNG xóa order items và reviews vì đó là dữ liệu lịch sử quan trọng
            
            // 1. Xóa cart items (giỏ hàng tạm thời)
            cartItemRepository.deleteByProductId(productId);
            
            // 2. KHÔNG xóa order items - giữ lại lịch sử đơn hàng
            // 3. KHÔNG xóa reviews - giữ lại đánh giá
            
            System.out.println("Cleaned up temporary data for product ID: " + productId);
            
        } catch (Exception e) {
            System.err.println("Error cleaning up product data for ID " + productId + ": " + e.getMessage());
            // Không throw exception, chỉ log lỗi
        }
    }

    // CATEGORY CRUD
    @Override
    public com.example.Web_sale_app.entity.Category createCategory(com.example.Web_sale_app.entity.Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public com.example.Web_sale_app.entity.Category updateCategory(Long id, com.example.Web_sale_app.entity.Category category) {
        com.example.Web_sale_app.entity.Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        com.example.Web_sale_app.entity.Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category không tồn tại"));
        categoryRepository.delete(category);
    }

    @Override
    public Optional<com.example.Web_sale_app.entity.Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // UC11 - Loại bỏ seller-specific methods hoặc chuyển thành generic
    @Override
    public ProductDTO createProductDraft(ProductDTO dto) {
        Product p = mapDtoToProduct(dto, false);
        p.setStatus(ProductStatus.DRAFT);
        p.setActive(false);
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }
    
    @Override
    public ProductDTO saveProductDraft(Long id, ProductDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (p.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể lưu nháp khi sản phẩm ở trạng thái nháp");
        }
        
        updateProductFromDto(p, dto);
        p.setActive(false);
        return mapToDTO(productRepository.save(p));
    }
    
    @Override
    public ProductDTO publishProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (p.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể xuất bản sản phẩm từ trạng thái nháp");
        }
        
        validateProductForPublishing(p);
        
        p.setStatus(ProductStatus.PUBLISHED);
        p.setActive(true);
        return mapToDTO(productRepository.save(p));
    }
    
    @Override
    public void hideProductTemporary(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (p.getStatus() != ProductStatus.PUBLISHED) {
            throw new IllegalStateException("Chỉ có thể ẩn sản phẩm đã xuất bản");
        }
        
        p.setStatus(ProductStatus.HIDDEN);
        p.setActive(false);
        productRepository.save(p);
    }
    
    @Override
    public void unhideProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (p.getStatus() != ProductStatus.HIDDEN) {
            throw new IllegalStateException("Chỉ có thể hiện lại sản phẩm đã ẩn");
        }
        
        p.setStatus(ProductStatus.PUBLISHED);
        p.setActive(true);
        productRepository.save(p);
    }
    
    @Override
    public ProductDTO cloneProduct(Long sourceProductId) {
        Product sourceProduct = productRepository.findById(sourceProductId)
                .orElseThrow(() -> new RuntimeException("Source product not found with id: " + sourceProductId));
        
        Product clonedProduct = new Product();
        clonedProduct.setName(sourceProduct.getName() + " (Copy)");
        clonedProduct.setDescription(sourceProduct.getDescription());
        clonedProduct.setPrice(sourceProduct.getPrice());
        clonedProduct.setStock(0);
        clonedProduct.setImageUrl(sourceProduct.getImageUrl());
        clonedProduct.setManufacturer(sourceProduct.getManufacturer());
        clonedProduct.setCategory(sourceProduct.getCategory());
        clonedProduct.setStatus(ProductStatus.DRAFT);
        clonedProduct.setActive(false);
        
        Product saved = productRepository.save(clonedProduct);
        return mapToDTO(saved);
    }
    
    @Override
    public Page<ProductDTO> getProductsByStatus(ProductStatus status, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::mapToDTO);
    }
    
    @Override
    public ProductDTO updateProductStatus(Long id, ProductStatus status) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        validateStatusTransition(p.getStatus(), status);
        
        p.setStatus(status);
        p.setActive(status == ProductStatus.PUBLISHED);
        
        return mapToDTO(productRepository.save(p));
    }
    
    // UC12 - STOCK MANAGEMENT (loại bỏ seller checks)
    @Override
    public ProductDTO updateProductStock(Long id, Integer newStock) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        validateStockQuantity(newStock);
        
        Integer oldStock = p.getStock();
        p.setStock(newStock);
        
        logStockChange(id, oldStock, newStock, "MANUAL_UPDATE");
        checkLowStockAlert(p);
        
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }
    
    @Override
    public List<ProductDTO> updateBatchStock(List<StockUpdateRequest> stockUpdates) {
        List<ProductDTO> updatedProducts = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        for (StockUpdateRequest request : stockUpdates) {
            try {
                Product p = productRepository.findById(request.productId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.productId()));
                
                validateStockQuantity(request.newStock());
                
                Integer oldStock = p.getStock();
                p.setStock(request.newStock());
                
                logStockChange(request.productId(), oldStock, request.newStock(), "BATCH_UPDATE");
                checkLowStockAlert(p);
                
                Product saved = productRepository.save(p);
                updatedProducts.add(mapToDTO(saved));
                
            } catch (Exception e) {
                errors.add("Sản phẩm ID " + request.productId() + ": " + e.getMessage());
            }
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Lỗi cập nhật hàng loạt: " + String.join("; ", errors));
        }
        
        return updatedProducts;
    }
    
    @Override
    public void configureLowStockAlert(Long productId, Integer threshold) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (threshold == null || threshold < 0) {
            throw new IllegalArgumentException("Ngưỡng cảnh báo phải >= 0");
        }
        
        p.setLowStockThreshold(threshold);
        productRepository.save(p);
        
        if (p.getStock() <= threshold) {
            triggerLowStockAlert(p);
        }
    }
    
    @Override
    public Page<ProductDTO> getLowStockProducts(Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            return cb.lessThanOrEqualTo(root.get("stock"), root.get("lowStockThreshold"));
        };
        
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::mapToDTO);
    }
    
    @Override
    public StockReport getStockReport() {
        List<Product> products = productRepository.findAll();
        
        int totalProducts = products.size();
        int inStockProducts = (int) products.stream().filter(p -> p.getStock() > 0).count();
        int outOfStockProducts = totalProducts - inStockProducts;
        int lowStockProducts = (int) products.stream()
                .filter(p -> p.getStock() <= (p.getLowStockThreshold() != null ? p.getLowStockThreshold() : 0))
                .count();
        
        BigDecimal totalStockValue = products.stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new StockReport(
            totalProducts,
            inStockProducts,
            outOfStockProducts,
            lowStockProducts,
            totalStockValue
        );
    }
    
    // Helper methods
    private void validateProductForPublishing(Product product) {
        List<String> errors = new ArrayList<>();
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            errors.add("Tên sản phẩm không được để trống");
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Giá sản phẩm phải lớn hơn 0");
        }
        
        if (product.getStock() == null || product.getStock() < 0) {
            errors.add("Số lượng tồn kho không được âm");
        }
        
        if (product.getCategory() == null) {
            errors.add("Sản phẩm phải thuộc một danh mục");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Lỗi validation: " + String.join(", ", errors));
        }
    }
    
    private void validateStatusTransition(ProductStatus from, ProductStatus to) {
        boolean isValidTransition = switch (from) {
            case DRAFT -> to == ProductStatus.PUBLISHED;
            case PUBLISHED -> to == ProductStatus.HIDDEN;
            case HIDDEN -> to == ProductStatus.PUBLISHED || to == ProductStatus.DRAFT;
        case OUT_OF_STOCK -> to == ProductStatus.PUBLISHED || to == ProductStatus.HIDDEN;

        };
        
        if (!isValidTransition) {
            throw new IllegalStateException(
                String.format("Không thể chuyển từ trạng thái %s sang %s", from.getDisplayName(), to.getDisplayName())
            );
        }
    }

    private void validateStockQuantity(Integer stock) {
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Số lượng tồn kho phải >= 0");
        }
        if (stock > 999999) {
            throw new IllegalArgumentException("Số lượng tồn kho không được vượt quá 999,999");
        }
    }
    
    private void logStockChange(Long productId, Integer oldStock, Integer newStock, String changeType) {
        System.out.println(String.format(
            "STOCK_CHANGE: Product %d, %d -> %d (%s)",
            productId, oldStock, newStock, changeType
        ));
    }
    
    private void checkLowStockAlert(Product product) {
        Integer threshold = product.getLowStockThreshold();
        if (threshold != null && product.getStock() <= threshold) {
            triggerLowStockAlert(product);
        }
    }
    
    private void triggerLowStockAlert(Product product) {
        System.out.println(String.format(
            "LOW_STOCK_ALERT: Product '%s' (ID: %d) has low stock: %d (threshold: %d)",
            product.getName(), product.getId(), product.getStock(), product.getLowStockThreshold()
        ));
    }

    // Không cần CSV import vì không có seller context
    // Có thể implement cho admin use case
}
