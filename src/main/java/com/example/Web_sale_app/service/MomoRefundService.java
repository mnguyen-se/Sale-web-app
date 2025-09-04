package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.Req.RefundRequest;
import com.example.Web_sale_app.dto.Res.RefundResponse;

public interface MomoRefundService {
    RefundResponse refund(RefundRequest req);
}
