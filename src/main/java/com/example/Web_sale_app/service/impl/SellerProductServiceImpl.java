package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.entity.ReqDTO.ReqProductDTO;
import com.example.Web_sale_app.repository.CategoryRepository;
import com.example.Web_sale_app.repository.ProductRepository;
import com.example.Web_sale_app.service.SellerProductService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SellerProductServiceImpl implements SellerProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public SellerProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Product addProduct(ReqProductDTO req) {
        boolean existsProduct = productRepository.existsByNameAndCategoryIdAndManufacturer(req.getName(), req.getCategoryId(), req.getManufacturer());
        if(existsProduct == false){
            Product newProduct = new Product();
            Category category = categoryRepository.findById(req.getCategoryId()).get();
            newProduct.setCategory(category);
            newProduct.setDescription(req.getDescription());
            newProduct.setManufacturer(req.getManufacturer());
            newProduct.setName(req.getName());
            newProduct.setPrice(req.getPrice());
            newProduct.setStock(req.getStock());
            return productRepository.save(newProduct);
        }
        return null;
    }

    @Override
    public Product updateProduct(Long productId, ReqProductDTO req) {
        return null;
    }

    @Override
    public void deleteProduct(Long productId) {

    }

    @Override
    public Product setActiveStatus(Long productId) {
        Product product = productRepository.findById(productId).get();
        if(product == null){
            throw new RuntimeException("Sản phẩm này không tồn tại.");
        }
        product.setActive(true);
        return product;
    }

    @Override
    public List<Product> findAllProductsIsActive(String sortField, String sortDir) {
        if(sortDir.equals("desc")){
            Sort sort = Sort.by(Sort.Direction.DESC, sortDir.toLowerCase());
            return productRepository.findAllByIsActiveTrue(sort);
        }
        Sort sort = Sort.by(Sort.Direction.ASC, sortDir.toLowerCase());
        return productRepository.findAllByIsActiveTrue(sort);
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
}
