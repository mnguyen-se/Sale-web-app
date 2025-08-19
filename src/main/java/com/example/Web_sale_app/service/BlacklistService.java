package com.example.Web_sale_app.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class BlacklistService {
    private final Set<String> blacklist = new HashSet<>();

    // thêm token vào blacklist
    public void addToken(String token) {
        blacklist.add(token);
    }

    // kiểm tra token có bị revoke chưa
    public boolean isTokenBlacklisted(String token) {
        return blacklist.contains(token);
    }
}
