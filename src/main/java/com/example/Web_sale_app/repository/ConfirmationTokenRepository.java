package com.example.Web_sale_app.repository;

import com.example.Web_sale_app.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
    
    /**
     * Delete all confirmation tokens for a specific user
     */
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
    
    /**
     * Alternative custom query for deletion
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ConfirmationToken ct WHERE ct.user.id = :userId")
    void deleteByUserIdCustom(@Param("userId") Long userId);
    
    /**
     * Find all tokens for a user
     */
    List<ConfirmationToken> findByUserId(Long userId);
    
    /**
     * Count tokens for a user
     */
    long countByUserId(Long userId);
}
