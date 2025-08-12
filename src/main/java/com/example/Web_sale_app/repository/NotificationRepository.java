package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
