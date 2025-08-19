package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.ReqDTO.ReqLoginDTO;
import com.example.Web_sale_app.entity.ReqDTO.ReqRegisterDTO;
import com.example.Web_sale_app.entity.User;

public interface AuthService {
    String verify(ReqLoginDTO reqLoginDTO);
    String register(ReqRegisterDTO req);
    boolean confirm(String token);
}
