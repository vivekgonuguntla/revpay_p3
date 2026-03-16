package com.revpay.card.repository;

import com.revpay.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUserId(Long userId);

    Optional<Card> findByUserIdAndIsDefaultTrue(Long userId);

    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    @Modifying
    @Query("UPDATE Card c SET c.isDefault = false WHERE c.userId = :userId AND c.isDefault = true")
    int clearDefaultForUser(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
