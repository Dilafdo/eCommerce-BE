package com.anymind.ecommerce.external.repository;

import com.anymind.ecommerce.domain.entity.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

    @Query("SELECT e FROM PaymentDetails e WHERE e.dateTime BETWEEN :startDate AND :endDate ORDER BY e.dateTime ASC")
    List<PaymentDetails> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
