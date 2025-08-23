package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.exception.ResourceNotFoundException;
import com.example.Web_sale_app.exception.ValidationException;
import com.example.Web_sale_app.exception.ForbiddenException;
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

    @Override
    public Page<ProductDTO> listProducts(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
    public Page<ProductDTO> listProductsForBuyer(Long categoryId, String q, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

          predicates.add(cb.equal(root.get("isActive"), true));

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
    public Optional<ProductDTO> getProductDetailDTO(Long id) {
        return productRepository.findById(id)
                .map(p -> new ProductDTO(
                        p.getId(),
                        p.getImageUrl(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getStock(),
                        p.getManufacturer(),
                        p.getCategory() != null ? p.getCategory().getName() : null
                ));
    }

    @Override
    public ProductDTO createProduct(ProductDTO dto, Long sellerId) {
        Product p = new Product();
        p.setName(dto.name());
        p.setImageUrl(dto.imageUrl());
        p.setManufacturer(dto.manufacturer());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setImageUrl(dto.imageUrl());

        if (dto.categoryName() != null) {
            categoryRepository.findByNameIgnoreCase(dto.categoryName())
                    .ifPresent(p::setCategory);
        }

        if (sellerId != null) {
            com.example.Web_sale_app.entity.User seller = new com.example.Web_sale_app.entity.User();
            seller.setId(sellerId);
            p.setSeller(seller);
        }

        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        // Optional: kiểm tra quyền sở hữu nếu cần (sellerId)
        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("Không có quyền sửa sản phẩm này");
        }

        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setImageUrl(dto.imageUrl());

        if (dto.categoryName() != null) {
            categoryRepository.findByNameIgnoreCase(dto.categoryName())
                    .ifPresent(p::setCategory);
        }

        return mapToDTO(productRepository.save(p));
    }

    @Override
    public void deleteProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("Không có quyền xóa sản phẩm này");
        }

        productRepository.delete(p);
    }

    @Override
    public Optional<ProductDTO> getProductByIdForEdit(Long id, Long sellerId) {
        return productRepository.findById(id)
                .filter(p -> sellerId == null || p.getSeller().getId().equals(sellerId))
                .map(this::mapToDTO);
    }

    private ProductDTO mapToDTO(Product p) {
        return new ProductDTO(
                p.getId(),
                p.getImageUrl(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getManufacturer(),
                p.getCategory() != null ? p.getCategory().getName() : null
        );
    }

    @Override
    public void hideProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("Không có quyền ẩn sản phẩm này");
        }

        p.setActive(false);
        productRepository.save(p);
    }

    @Override
    public void unhideProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        if (sellerId != null && !p.getSeller().getId().equals(sellerId)) {
            throw new ForbiddenException("Không có quyền hiển thị lại sản phẩm này");
        }

        p.setActive(true);
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
    public ProductDTO createProductAsAdmin(ProductDTO dto) {
        Product p = new Product();
        p.setName(dto.name());
        p.setImageUrl(dto.imageUrl());
        p.setManufacturer(dto.manufacturer());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setActive(true); // Admin tạo sản phẩm mặc định active

        if (dto.categoryName() != null) {
            categoryRepository.findByNameIgnoreCase(dto.categoryName())
                    .ifPresent(p::setCategory);
        }

        Product saved = productRepository.save(p);
        return mapToDTO(saved);
    }

    @Override
    public ProductDTO updateProductAsAdmin(Long id, ProductDTO dto) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

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

        return mapToDTO(productRepository.save(p));
    }

    @Override
    public void deleteProductAsAdmin(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        productRepository.delete(p);
    }

    @Override
    public void toggleProductActiveStatus(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        p.setActive(!p.isActive());
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
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        
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

}