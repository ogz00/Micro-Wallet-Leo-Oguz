package com.oguz.demo.microwallet.repository;

import com.oguz.demo.microwallet.entity.Transaction;
import com.oguz.demo.microwallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM transaction WHERE wallet_id = ? ORDER By created_at DESC LIMIT  1")
    Transaction findTopTransaction(Long walletId);

    Optional<Transaction> findBySuppliedId(Long suppliedId);

    List<Transaction> findAllByWallet(Wallet wallet);
}
