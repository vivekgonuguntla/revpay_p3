package com.revpay.wallet.repository;

import com.revpay.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.senderWalletId = :walletId OR t.receiverWalletId = :walletId ORDER BY t.timestamp DESC")
    List<Transaction> findAllByWalletId(@Param("walletId") Long walletId);

    List<Transaction> findBySenderWalletIdOrReceiverWalletIdOrderByTimestampDesc(Long senderWalletId, Long receiverWalletId);
}
