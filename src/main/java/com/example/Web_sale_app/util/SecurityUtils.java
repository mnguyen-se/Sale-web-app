package com.example.Web_sale_app.util;

import com.example.Web_sale_app.entity.User;
import com.example.Web_sale_app.entity.UserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils(){}

    public static Long currentUserIdOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User ud) {
            return ud.getId(); // chú ý: UserDetail cần có getter getId()
        }
        return null;
    }
}
