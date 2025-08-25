package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.ProductDTO;
import com.example.Web_sale_app.dto.StockImportResult;
import com.example.Web_sale_app.dto.StockReport;
import com.example.Web_sale_app.dto.Req.StockUpdateRequest;
import com.example.Web_sale_app.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for UC12 - Stock Management
 * Handles inventory operations for sellers
 */
@Tag(name = "Stock Management", description = "API quản lý tồn kho cho seller")
@RestController
@RequestMapping("/api/seller/stock")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
public class StockManagementController {

    private final CatalogService catalogService;

    // ===== INDIVIDUAL STOCK UPDATE =====

    /**
     * UC12 - Cập nhật tồn kho đơn lẻ
     */
    @Operation(
        summary = "Cập nhật tồn kho sản phẩm",
        description = "Cập nhật số lượng tồn kho cho một sản phẩm cụ thể"
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    @ApiResponse(responseCode = "403", description = "Không có quyền")
    @ApiResponse(responseCode = "409", description = "Có đơn hàng đang xử lý")
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProductStock(
            @Parameter(description = "ID sản phẩm", example = "1")
            @PathVariable Long productId,
            
            @Parameter(description = "Số lượng tồn kho mới", example = "100")
            @RequestBody Map<String, Integer> request,
            
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            Integer newStock = request.get("stock");
            
            if (newStock == null) {
                return ResponseEntity.badRequest().build();
            }
            
            ProductDTO updated = catalogService.updateProductStock(productId, newStock, sellerId);
            return ResponseEntity.ok(updated);
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ===== BATCH STOCK UPDATE =====

    /**
     * UC12 - Cập nhật tồn kho hàng loạt
     */
    @Operation(
        summary = "Cập nhật tồn kho hàng loạt",
        description = "Cập nhật tồn kho cho nhiều sản phẩm cùng lúc"
    )
    @PostMapping("/batch-update")
    public ResponseEntity<List<ProductDTO>> updateBatchStock(
            @Valid @RequestBody List<StockUpdateRequest> stockUpdates,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            List<ProductDTO> updated = catalogService.updateBatchStock(stockUpdates, sellerId);
            return ResponseEntity.ok(updated);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ===== CSV IMPORT =====

    /**
     * UC12 - Import tồn kho từ file CSV
     */
    @Operation(
        summary = "Import tồn kho từ CSV",
        description = "Import dữ liệu tồn kho từ file CSV. Format: ProductID,NewStock"
    )
    @PostMapping(value = "/import-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StockImportResult> importStockFromCsv(
            @Parameter(description = "File CSV chứa dữ liệu tồn kho")
            @RequestParam("file") MultipartFile file,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .header("Error-Message", "File không được để trống")
                        .build();
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                return ResponseEntity.badRequest()
                        .header("Error-Message", "Chỉ chấp nhận file CSV")
                        .build();
            }
            
            String csvContent = new String(file.getBytes());
            StockImportResult result = catalogService.importStockFromCsv(csvContent, sellerId);
            
            return ResponseEntity.ok(result);
            
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", "Lỗi đọc file: " + e.getMessage())
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        }
    }

    /**
     * Download CSV template for stock import
     */
    @Operation(summary = "Tải template CSV")
    @GetMapping("/csv-template")
    public ResponseEntity<String> downloadCsvTemplate() {
        String template = "ProductID,NewStock\n1,100\n2,50\n3,200";
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=stock_template.csv")
                .header("Content-Type", "text/csv")
                .body(template);
    }

    // ===== LOW STOCK ALERTS =====

    /**
     * UC12 - Cấu hình cảnh báo tồn kho thấp
     */
    @Operation(
        summary = "Cấu hình cảnh báo tồn kho thấp",
        description = "Thiết lập ngưỡng cảnh báo khi tồn kho thấp"
    )
    @PostMapping("/{productId}/low-stock-alert")
    public ResponseEntity<Void> configureLowStockAlert(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> request,
            Authentication auth) {
        try {
            Long sellerId = getSellerId(auth);
            Integer threshold = request.get("threshold");
            
            if (threshold == null) {
                return ResponseEntity.badRequest().build();
            }
            
            catalogService.configureLowStockAlert(productId, threshold, sellerId);
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * UC12 - Lấy danh sách sản phẩm tồn kho thấp
     */
    @Operation(
        summary = "Danh sách sản phẩm tồn kho thấp",
        description = "Lấy danh sách sản phẩm có tồn kho <= ngưỡng cảnh báo"
    )
    @GetMapping("/low-stock")
    public ResponseEntity<Page<ProductDTO>> getLowStockProducts(
            Authentication auth,
            Pageable pageable) {
        Long sellerId = getSellerId(auth);
        Page<ProductDTO> lowStockProducts = catalogService.getLowStockProducts(sellerId, pageable);
        return ResponseEntity.ok(lowStockProducts);
    }

    // ===== STOCK REPORTS =====

    /**
     * UC12 - Báo cáo tồn kho tổng quan
     */
    @Operation(
        summary = "Báo cáo tồn kho",
        description = "Lấy báo cáo tổng quan về tình trạng tồn kho"
    )
    @GetMapping("/report")
    public ResponseEntity<StockReport> getStockReport(Authentication auth) {
        Long sellerId = getSellerId(auth);
        StockReport report = catalogService.getStockReport(sellerId);
        return ResponseEntity.ok(report);
    }

    /**
     * Lấy thống kê tồn kho chi tiết
     */
    @Operation(summary = "Thống kê tồn kho chi tiết")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStockStatistics(Authentication auth) {
        Long sellerId = getSellerId(auth);
        StockReport report = catalogService.getStockReport(sellerId);
        
        Map<String, Object> statistics = Map.of(
                "totalProducts", report.totalProducts(),
                "inStockProducts", report.inStockProducts(),
                "outOfStockProducts", report.outOfStockProducts(),
                "lowStockProducts", report.lowStockProducts(),
                "totalStockValue", report.totalStockValue(),
                "inStockPercentage", report.totalProducts() > 0 
                    ? (double) report.inStockProducts() / report.totalProducts() * 100 
                    : 0.0,
                "outOfStockPercentage", report.totalProducts() > 0 
                    ? (double) report.outOfStockProducts() / report.totalProducts() * 100 
                    : 0.0
        );
        
        return ResponseEntity.ok(statistics);
    }

    // ===== HELPER METHODS =====

    /**
     * Extract seller ID from authentication
     */
    private Long getSellerId(Authentication auth) {
        // Implementation depends on your authentication setup
        return Long.valueOf(auth.getName()); // Adjust based on your auth implementation
    }
}
