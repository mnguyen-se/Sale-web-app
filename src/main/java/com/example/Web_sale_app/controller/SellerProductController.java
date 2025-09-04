package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.entity.ReqDTO.ReqProductDTO;
import com.example.Web_sale_app.entity.ResDTO.ResProductDTO;
import com.example.Web_sale_app.service.SellerProductService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller/product")
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {
    private final SellerProductService sellerProductService;
    public SellerProductController(SellerProductService sellerProductService) {
        this.sellerProductService = sellerProductService;
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(String sortDir,String sortField){
        return ResponseEntity.ok().body(sellerProductService.findAllProductsIsActive(sortDir,sortField));
    }

    @PostMapping("/add")
    public ResponseEntity<ResProductDTO> addProduct(@RequestBody ReqProductDTO reqProductDTO){
        ResProductDTO res = new ResProductDTO();
        Product product = sellerProductService.addProduct(reqProductDTO);

        res.setCategoryName(product.getCategory().getName());
        res.setManufacturer(product.getManufacturer());
        res.setName(product.getName());
        res.setPrice(product.getPrice());
        res.setImageUrl(product.getImageUrl());
        res.setDescription(product.getDescription());
        res.setStock(product.getStock());

        return ResponseEntity.ok().body(res);
    }

    @PostMapping("/update")
    public ResponseEntity<ResProductDTO> updateProduct(@RequestParam long productId, @RequestBody ReqProductDTO reqProductDTO){
        Product product = sellerProductService.updateProduct(productId, reqProductDTO);
        ResProductDTO res = new ResProductDTO();
        res.setCategoryName(product.getCategory().getName());
        res.setManufacturer(product.getManufacturer());
        res.setName(product.getName());
        res.setPrice(product.getPrice());
        res.setImageUrl(product.getImageUrl());
        res.setDescription(product.getDescription());
        res.setStock(product.getStock());
        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(@RequestParam long productId){
        sellerProductService.deleteProduct(productId);
        return ResponseEntity.ok("Delete product successfully!");
    }

}
