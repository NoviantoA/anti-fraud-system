package com.novianto.antifraud.system.repository;

import com.novianto.antifraud.system.entity.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByDateBetweenAndNumber(LocalDateTime start, LocalDateTime end, String number);
    List<Transaction> findAllByNumber(String number);
}
