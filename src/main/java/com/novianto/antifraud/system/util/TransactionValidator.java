package com.novianto.antifraud.system.util;

import com.novianto.antifraud.system.entity.Card;
import com.novianto.antifraud.system.entity.transaction.Transaction;
import com.novianto.antifraud.system.entity.transaction.TransactionResult;
import com.novianto.antifraud.system.repository.CardRepository;
import com.novianto.antifraud.system.repository.SuspiciousIPRepository;
import com.novianto.antifraud.system.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@SuppressWarnings("unused")
public class TransactionValidator {

    private Transaction transaction;

    @Autowired
    private SuspiciousIPRepository suspiciousIPRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private Set<String> info = new TreeSet<>();

    /**
     * Mengecek apakah feedback sesuai dengan hasil transaksi.
     *
     * @param feedback Feedback untuk check
     * @return True jika feedback sesuai dengan hasil transaksi, false jika sebaliknya.
     */
    public static boolean isFeedbackWrongFormat(String feedback){
        return Arrays.stream(TransactionResult.values()).noneMatch(transactionResult -> transactionResult.name().equals(feedback));
    }

    /**
     * Menerima transaksi baru dan memverifikasinya berdasarkan heuristik tertentu (misalkan IP mencurigakan, kartu curian, dll.).
     *
     * @param transaction Transaction untuk memverifikasi.
     */
    public void verifyTransaction(Transaction transaction){
        this.transaction = transaction;
        this.info = new TreeSet<>();

        transaction.setResult(TransactionResult.ALLOWED);

        // Heuristik untuk transaksi
        checkIfStolenCard();
        checkIfSuspiciousIP();
        checkIfCorrelationExists();
        checkIfAmountIsTooHigh();

        transaction.setInfo(formatInfo());
    }

    /**
     * Memeriksa apakah nomor kartu transaksi ditandai sebagai dicuri dalam database.
     */
    private void checkIfStolenCard(){
        if (cardRepository.existsByNumberAndIsLockedTrue(transaction.getNumber())){
            transaction.setResult(TransactionResult.PROHIBITED);
            info.add("card-number");
        }
    }

    /**
     * Memeriksa apakah alamat IP transaksi ditandai sebagai mencurigakan di database.
     */
    private void checkIfSuspiciousIP(){
        if (suspiciousIPRepository.findByIp(transaction.getIp()).isPresent()){
            transaction.setResult(TransactionResult.PROHIBITED);
            info.add("ip");
        }
    }

    /**
     * Memeriksa apakah transaksi berkorelasi dengan transaksi lain berdasarkan wilayah dan alamat ip.
     */
    private void checkIfCorrelationExists(){
        List<Transaction> timeBeforeTransaction = transactionRepository.findAllByDateBetweenAndNumber(
                transaction.getDate().minusHours(1),
                transaction.getDate(),
                transaction.getNumber()
        );

        long regionCount = timeBeforeTransaction.stream()
                .map(Transaction::getRegion)
                .filter(region -> !Objects.equals(region, transaction.getRegion()))
                .distinct().count();

        long ipCount = timeBeforeTransaction.stream()
                .map(Transaction::getIp)
                .filter(ip -> !Objects.equals(ip, transaction.getIp()))
                .distinct().count();

        if (regionCount == 2 && !Objects.equals(transaction.getResult(), TransactionResult.ALLOWED.name())){
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("region-correlation");
        }

        if (ipCount == 2 && !Objects.equals(transaction.getResult(), TransactionResult.PROHIBITED.name())){
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("ip-correlation");
        }

        if (regionCount > 2){
            transaction.setResult(TransactionResult.PROHIBITED);
            info.add("region-correlation");
        }

        if (ipCount > 2){
            transaction.setResult(TransactionResult.PROHIBITED);
            info.add("ip-correlation");
        }
    }


    /**
    * Memeriksa apakah jumlah transaksi terlalu tinggi berdasarkan limit kartu pelanggan.
    */
    private void checkIfAmountIsTooHigh(){
        Card card = cardRepository.findByNumber(transaction.getNumber()).orElseThrow(AssertionError::new);

        int allowedLimit = card.getAllowedLimit();
        int manualLimit = card.getManualLimit();

        if (transaction.getAmount() > allowedLimit && transaction.getAmount() <= manualLimit && !Objects.equals(transaction.getResult(), TransactionResult.PROHIBITED.name())){
            transaction.setResult(TransactionResult.MANUAL_PROCESSING);
            info.add("amount");
        }

        if (transaction.getAmount() > manualLimit){
            if ((!Objects.equals(transaction.getResult(), TransactionResult.PROHIBITED.name()))){
                info.clear();
            }
            info.add("amount");
            transaction.setResult(TransactionResult.PROHIBITED);
        }
    }

    /**
     * Helper Method yang memformat info transaksi.
     *
     * @return Formatted info dari transaction
     *
     */
    private String formatInfo(){
        if (Objects.equals(transaction.getResult(), TransactionResult.ALLOWED.name())){
            info.add("none");
        }
        return String.join(", ", info);
    }
}
