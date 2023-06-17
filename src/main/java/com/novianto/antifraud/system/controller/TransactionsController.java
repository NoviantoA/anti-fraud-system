package com.novianto.antifraud.system.controller;

import com.novianto.antifraud.system.entity.transaction.Transaction;
import com.novianto.antifraud.system.logger.Logger;
import com.novianto.antifraud.system.repository.TransactionRepository;
import com.novianto.antifraud.system.service.TransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Ini adalah transaction controller untuk transaksi.
 * Menerima request untuk operasi tertentu dan meneruskannya ke layer service.
 * Hasil operasi dikembalikan ke klien.
 * Peringatan 'bidang yang tidak digunakan' disembunyikan karena bidang diisi secara otomatis saat runtime.
 */

@RestController
@RequestMapping("/api/antifraud")
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class TransactionsController {

    // @Autowired
    private TransactionsService transactionsService;

    // @Autowired
    private TransactionRepository transactionRepository;

    // get list all transaction
    @PostMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<?> validateTransaction(@RequestBody Transaction transaction){
        String endpoint = "/api/antifraud/transaction";
        Logger.logRequest(HttpMethod.POST, endpoint, transaction);

        Map<String, String> response = transactionsService.processTransaction(transaction);

        Logger.logResponse(HttpMethod.POST, endpoint, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // feedback transaction
    @PutMapping(value = "/transaction", consumes = "application/json")
    public ResponseEntity<?> provideFeedback(@RequestBody Map<String, String> transactionFeedback){
        String endpoint = "/api/antifraud/transaction";
        Logger.logRequest(HttpMethod.PUT, endpoint, transactionFeedback);

        long transactionId = Long.parseLong(transactionFeedback.get("transactionId"));
        String feedback = transactionFeedback.get("feedback");
        Transaction transaction = transactionsService.updateTransaction(transactionId, feedback);

        Logger.logResponse(HttpMethod.PUT, endpoint, transaction);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    // get list all transaction
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(){
        String endpoint = "/api/antifraud/history";

        List<Transaction> transactions = transactionRepository.findAll();
        Logger.logResponse(HttpMethod.GET, endpoint, transactions);

        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    // get all list transaction by card number
    @GetMapping("/history/{number}")
    public ResponseEntity<?> getHistoryForCardNumber(@PathVariable String number){
        String endpoint = "/api/antifraud/history/" + number;
        Logger.logRequest(HttpMethod.GET, endpoint, number);

        List<Transaction> transactions = transactionsService.getTransactionHistory(number);

        Logger.logResponse(HttpMethod.GET, endpoint, transactions);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
