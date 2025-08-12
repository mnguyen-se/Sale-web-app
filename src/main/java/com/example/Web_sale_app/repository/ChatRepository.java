package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUser_IdOrderByIdAsc(Long userId);
}
