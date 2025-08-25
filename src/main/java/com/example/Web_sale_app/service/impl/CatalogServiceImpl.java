package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.repository.CategoryRepository;
import com.example.Web_sale_app.repository.ProductRepository;
import com.example.Web_sale_app.service.CatalogService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogServiceImpl implements CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
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
    
    // Hàm private dùng chung để map Product sang ProductDTO
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

    // Hàm private dùng chung để map ProductDTO sang Product (tạo mới)
    private Product mapDtoToProduct(ProductDTO dto, Long sellerId, boolean isAdmin) {
        Product p = new Product();
        p.setName(dto.name());
        p.setImageUrl(dto.imageUrl());
        p.setManufacturer(dto.manufacturer());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        // Admin tạo mặc định active, seller thì có thể set sau
        p.setActive(isAdmin || p.isActive());

        if (dto.categoryName() != null) {
            categoryRepository.findByNameIgnoreCase(dto.categoryName())
                    .ifPresent(p::setCategory);
        }

        if (!isAdmin && sellerId != null) {
            com.example.Web_sale_app.entity.User seller = new com.example.Web_sale_app.entity.User();
            seller.setId(sellerId);
            p.setSeller(seller);
        }
        return p;
    }

    // Hàm private dùng chung để cập nhật Product từ DTO
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

    // CREATE
    @Override
    public ProductDTO createProduct(ProductDTO dto, Long sellerId) {
        Product p = mapDtoToProduct(dto, sellerId, false);
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }

    @Override
    public ProductDTO createProductAsAdmin(ProductDTO dto) {
        Product p = mapDtoToProduct(dto, null, true);
        p.setActive(true); // Admin tạo mặc định active
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }

    // UPDATE
    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new SecurityException("Không có quyền sửa sản phẩm này");
        }
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

    @Override
    public void deleteProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new SecurityException("Không có quyền xóa sản phẩm này");
        }
        productRepository.delete(p);
    }

    @Override
    public void deleteProductAsAdmin(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.delete(p);
    }

    @Override
    public void hideProduct(Long id, Long sellerId) {
        // Cập nhật để sử dụng logic UC11
        hideProductTemporary(id, sellerId);
    }

    // XÓA method này - bị trùng với UC11
    // @Override
    // public void unhideProduct(Long id, Long sellerId) {
    //     updateProductActiveStatus(id, sellerId, true);
    // }
    
    // Hàm private dùng chung để cập nhật trạng thái active của sản phẩm
    private void updateProductActiveStatus(Long id, Long sellerId, boolean isActive) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new SecurityException("Không có quyền thay đổi trạng thái sản phẩm này");
        }

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

    // ===== ADMIN CRUD METHODS =====
    
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

    /**
     * Admin method to hide product without seller authorization check
     */
    @Override
    public void hideProductAsAdmin(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        p.setStatus(ProductStatus.HIDDEN);
        p.setActive(false);
        productRepository.save(p);
    }

    /**
     * Admin method to unhide product without seller authorization check
     */
    @Override
    public void unhideProductAsAdmin(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        // Admin có thể unhide từ bất kỳ trạng thái nào thành PUBLISHED
        p.setStatus(ProductStatus.PUBLISHED);
        p.setActive(true);
        productRepository.save(p);
    }

    // ===== CATEGORY CRUD FOR ADMIN =====
    
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

    // UC11 - Seller Product Management Implementation
    
    /**
     * Tạo sản phẩm mới ở trạng thái nháp
     */
    @Override
    public ProductDTO createProductDraft(ProductDTO dto, Long sellerId) {
        Product p = mapDtoToProduct(dto, sellerId, false);
        p.setStatus(ProductStatus.DRAFT);
        p.setActive(false); // Nháp chưa active
        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }
    
    /**
     * Lưu nháp sản phẩm (cập nhật mà không xuất bản)
     */
    @Override
    public ProductDTO saveProductDraft(Long id, ProductDTO dto, Long sellerId) {
        Product p = findProductWithSellerCheck(id, sellerId);
        
        // Chỉ cho phép lưu nháp khi đang ở trạng thái DRAFT
        if (p.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể lưu nháp khi sản phẩm ở trạng thái nháp");
        }
        
        updateProductFromDto(p, dto);
        p.setActive(false); // Đảm bảo nháp không active
        return mapToDTO(productRepository.save(p));
    }
    
    /**
     * Xuất bản sản phẩm từ nháp
     */
    @Override
    public ProductDTO publishProduct(Long id, Long sellerId) {
        Product p = findProductWithSellerCheck(id, sellerId);
        
        if (p.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể xuất bản sản phẩm từ trạng thái nháp");
        }
        
        // Validate required fields before publishing
        validateProductForPublishing(p);
        
        p.setStatus(ProductStatus.PUBLISHED);
        p.setActive(true);
        return mapToDTO(productRepository.save(p));
    }
    
    /**
     * Ẩn sản phẩm tạm thời
     */
    @Override
    public void hideProductTemporary(Long id, Long sellerId) {
        Product p = findProductWithSellerCheck(id, sellerId);
        
        if (p.getStatus() != ProductStatus.PUBLISHED) {
            throw new IllegalStateException("Chỉ có thể ẩn sản phẩm đã xuất bản");
        }
        
        p.setStatus(ProductStatus.HIDDEN);
        p.setActive(false);
        productRepository.save(p);
    }
    
    /**
     * Hiện lại sản phẩm đã ẩn - METHOD CHÍNH (giữ lại)
     */
    @Override
    public void unhideProduct(Long id, Long sellerId) {
        Product p = findProductWithSellerCheck(id, sellerId);
        
        if (p.getStatus() != ProductStatus.HIDDEN) {
            throw new IllegalStateException("Chỉ có thể hiện lại sản phẩm đã ẩn");
        }
        
        p.setStatus(ProductStatus.PUBLISHED);
        p.setActive(true);
        productRepository.save(p);
    }
    
    /**
     * Clone sản phẩm từ sản phẩm khác
     */
    @Override
    public ProductDTO cloneProduct(Long sourceProductId, Long sellerId) {
        Product sourceProduct = productRepository.findById(sourceProductId)
                .orElseThrow(() -> new RuntimeException("Source product not found with id: " + sourceProductId));
        
        // Tạo bản copy
        Product clonedProduct = new Product();
        clonedProduct.setName(sourceProduct.getName() + " (Copy)");
        clonedProduct.setDescription(sourceProduct.getDescription());
        clonedProduct.setPrice(sourceProduct.getPrice());
        clonedProduct.setStock(0); // Reset stock cho bản copy
        clonedProduct.setImageUrl(sourceProduct.getImageUrl());
        clonedProduct.setManufacturer(sourceProduct.getManufacturer());
        clonedProduct.setCategory(sourceProduct.getCategory());
        
        // Set seller và trạng thái nháp
        com.example.Web_sale_app.entity.User seller = new com.example.Web_sale_app.entity.User();
        seller.setId(sellerId);
        clonedProduct.setSeller(seller);
        clonedProduct.setStatus(ProductStatus.DRAFT);
        clonedProduct.setActive(false);
        
        Product saved = productRepository.save(clonedProduct);
        return mapToDTO(saved);
    }
    
    /**
     * Lấy danh sách sản phẩm theo trạng thái của seller
     */
    @Override
    public Page<ProductDTO> getProductsByStatusForSeller(Long sellerId, ProductStatus status, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("seller").get("id"), sellerId));
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(this::mapToDTO);
    }
    
    /**
     * Cập nhật trạng thái sản phẩm
     */
    @Override
    public ProductDTO updateProductStatus(Long id, ProductStatus status, Long sellerId) {
        Product p = findProductWithSellerCheck(id, sellerId);
        
        // Validate transition rules
        validateStatusTransition(p.getStatus(), status);
        
        p.setStatus(status);
        p.setActive(status == ProductStatus.PUBLISHED);
        
        return mapToDTO(productRepository.save(p));
    }
    
    // Helper methods
    
    /**
     * Tìm sản phẩm và kiểm tra quyền seller
     */
    private Product findProductWithSellerCheck(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        if (!p.getSeller().getId().equals(sellerId)) {
            throw new SecurityException("Không có quyền thao tác với sản phẩm này");
        }
        
        return p;
    }
    
    /**
     * Validate sản phẩm trước khi xuất bản
     */
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
    
    /**
     * Validate chuyển trạng thái hợp lệ
     */
    private void validateStatusTransition(ProductStatus from, ProductStatus to) {
        // Định nghĩa các chuyển trạng thái hợp lệ
        boolean isValidTransition = switch (from) {
            case DRAFT -> to == ProductStatus.PUBLISHED;
            case PUBLISHED -> to == ProductStatus.HIDDEN;
            case HIDDEN -> to == ProductStatus.PUBLISHED || to == ProductStatus.DRAFT;
        };
        
        if (!isValidTransition) {
            throw new IllegalStateException(
                String.format("Không thể chuyển từ trạng thái %s sang %s", from.getDisplayName(), to.getDisplayName())
            );
        }
    }

}
