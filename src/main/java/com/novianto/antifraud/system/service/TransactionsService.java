package com.novianto.antifraud.system.service;

import com.novianto.antifraud.system.entity.Card;
import com.novianto.antifraud.system.entity.transaction.Transaction;
import com.novianto.antifraud.system.entity.transaction.TransactionResult;
import com.novianto.antifraud.system.repository.CardRepository;
import com.novianto.antifraud.system.repository.TransactionRepository;
import com.novianto.antifraud.system.util.CardValidator;
import com.novianto.antifraud.system.util.IPAddressValidator;
import com.novianto.antifraud.system.util.TransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("unused")
public class TransactionsService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionValidator transactionValidator;

    /**
     * Memproses transaksi dengan memvalidasi nomor kartu dan alamat ip.
     */
    public Map<String, String> processTransaction(Transaction transaction){
        Long amount = transaction.getAmount();
        // check apakak ip adress valid
        String ip = IPAddressValidator.isNonValidIp(transaction.getIp()) ? null : transaction.getIp();
        // check apakah card number valid
        String cardNumber = CardValidator.isNonValid(transaction.getNumber()) ? null : transaction.getNumber();

        // transfer negative amount tidak diizinkan
        if (amount == null || ip == null || cardNumber == null || amount <= 0 || ip.isEmpty() || cardNumber.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Transaction");
        }

        // check apakah card sudah tersedia di database
        saveCardIfNotExists(cardNumber);

        // verifikasi transaksi dan simpan ke database
        transactionValidator.verifyTransaction(transaction);
        transactionRepository.save(transaction);

        return Map.of(
                "result", transaction.getResult(),
                "info", transaction.getInfo()
        );
    }

    /**
     * Helper method untuk menyimpan card di database jika tidak ada.
     */
    private void saveCardIfNotExists(String cardNumber){
        if (cardRepository.findByNumber(cardNumber).isEmpty()){
            cardRepository.save(new Card(cardNumber, false));
        }
    }

    /**
     * Provide feedback untuk transaksi mencurigakan.
     */

    public Transaction updateTransaction(long transactionId, String feedback){
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction Not Found"));

        if (TransactionValidator.isFeedbackWrongFormat(feedback)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Feedback");
        } else if (!transaction.getFeedback().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction Already Processed");
        } else if (transaction.getResult().equals(feedback)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Transaction Not Processed");
        }

        changeLimit(transaction, feedback);

        transaction.setFeedback(TransactionResult.valueOf(feedback));
        transactionRepository.save(transaction);
        return transaction;
    }

    /**
     * Method Helper untuk mengubah batas transaksi tertentu berdasarkan umpan balik yang diberikan.
     */
    private void changeLimit(Transaction transaction, String feedback){
        String transactionResult = transaction.getResult();
        Card card = cardRepository.findByNumber(transaction.getNumber())
                .orElseThrow(AssertionError::new);

        // Rumus untuk menambah limit: new_limit = 0.8 * current_limit + 0.2 * value_from_transaction
        int increasedAllowed = (int) Math.ceil(0.8 * card.getAllowedLimit() + 0.2 * transaction.getAmount());
        int decreasedAllowed = (int) Math.ceil(0.8 * card.getAllowedLimit() - 0.2 * transaction.getAmount());
        int increaseManual = (int) Math.ceil(0.8 * card.getManualLimit() + 0.2 * transaction.getAmount());
        int decreaseManual = (int) Math.ceil(0.8 * card.getManualLimit() - 0.2 * transaction.getAmount());

        // set new limit berdasarkan feedback
        if (feedback.equals("MANUAL_PROCESSING") && transactionResult.equals("ALLOWED")){
            card.setAllowedLimit(decreasedAllowed);
        } else if (feedback.equals("PROHIBITED") && transactionResult.equals("ALLOWED")){
            card.setAllowedLimit(decreasedAllowed);
            card.setManualLimit(decreaseManual);
        } else if (feedback.equals("ALLOWED") && transactionResult.equals("MANUAL_PROCESSING")) {
            card.setAllowedLimit(increasedAllowed);
        } else if (feedback.equals("PROHIBITED") && transactionResult.equals("MANUAL_PROCESSING")){
            card.setManualLimit(decreaseManual);
        } else if (feedback.equals("ALLOWED") && transactionResult.equals("PROHIBITED")) {
            card.setAllowedLimit(increasedAllowed);
            card.setManualLimit(increaseManual);
        } else if (feedback.equals("MANUAL_PROCESSING") && transactionResult.equals("PROHIBITED")) {
            card.setManualLimit(increaseManual);
        }

        // save new limit ke database
        cardRepository.save(card);
    }

    /**
     * Return transaction history untuk nomor kartu yang diberikan
     */
    public List<Transaction> getTransactionHistory(String cardNumber){
        if (CardValidator.isNonValid(cardNumber)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<Transaction> transactions = transactionRepository.findAllByNumber(cardNumber);
        if (transactions.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return transactions;
    }
}
