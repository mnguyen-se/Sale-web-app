// com.example.Web_sale_app.dto.GuestOrderLookupRequest
package com.example.Web_sale_app.dto.Req;

public record GuestOrderLookupRequest(
        Long orderId,
        String email,  // có thể null
        String phone   // có thể null
) {}
