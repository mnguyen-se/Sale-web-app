package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.entity.ReqDTO.ReqProductDTO;

import java.util.List;

public interface SellerProductService {
    Product addProduct(ReqProductDTO req);
    Product updateProduct(Long productId, ReqProductDTO req);
    void deleteProduct(Long productId);
    Product setActiveStatus(Long productId);
    List<Product> findAllProductsIsActive(String sortField, String sortDir);
    List<Product> findAllProducts();
}
