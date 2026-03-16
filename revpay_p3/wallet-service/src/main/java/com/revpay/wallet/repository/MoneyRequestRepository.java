package com.revpay.wallet.repository;

import com.revpay.wallet.entity.MoneyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    @Query("SELECT mr FROM MoneyRequest mr WHERE mr.requesterId = :userId OR mr.payerId = :userId ORDER BY mr.createdAt DESC")
    List<MoneyRequest> findAllByUserId(@Param("userId") Long userId);

    List<MoneyRequest> findByRequesterIdOrPayerIdOrderByCreatedAtDesc(Long requesterId, Long payerId);
}
