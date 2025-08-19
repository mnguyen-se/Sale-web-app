package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;

public interface AuthService {
    String verify(ReqLoginDTO reqLoginDTO);
}
