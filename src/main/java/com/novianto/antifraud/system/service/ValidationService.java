package com.novianto.antifraud.system.service;

import com.novianto.antifraud.system.entity.Card;
import com.novianto.antifraud.system.entity.IPAddress;
import com.novianto.antifraud.system.repository.CardRepository;
import com.novianto.antifraud.system.repository.SuspiciousIPRepository;
import com.novianto.antifraud.system.util.CardValidator;
import com.novianto.antifraud.system.util.IPAddressValidator;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@SuppressWarnings("unused")
public class ValidationService {

    @Autowired
    private SuspiciousIPRepository suspiciousIPRepository;

    @Autowired
    private CardRepository cardRepository;

    // save IP mencurigakan untuk mencegah transaksi lebih lanjut
    public IPAddress saveSuspiciousIP(String ip){
        if (IPAddressValidator.isNonValidIp(ip)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid IP Address");
        }

        if (suspiciousIPRepository.findByIp(ip).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Card Number terdeteksi tercuri");
        }

        IPAddress ipAddress = new IPAddress(ip);
        suspiciousIPRepository.save(ipAddress);

        return ipAddress;
    }

    // return semua IP Address yang mencurigakan dan simpam
    public List<IPAddress> getSuspiciousIP(){
        return suspiciousIPRepository.findAll();
    }

    // remove IP Address yang mencurigakan dari database
    public String deleteSuspiciousIP(String ip){
        if (IPAddressValidator.isNonValidIp(ip)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid IP Address");

        IPAddress ipAddress = suspiciousIPRepository.findByIp(ip)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IP Address Not Found"));

        suspiciousIPRepository.delete(ipAddress);

        return "IP " + ip + " successfully removed!";
    }

    // save card yang berpotensi terdeteksi tercuri untuk mencegah transaksi lebih lanjut agar mengurangi resiko penipuan bagi custmer
    public Card saveStolenCard(String cardNumber){
        if (CardValidator.isNonValid(cardNumber)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Card Number");
        }

        Card card;

        // check apakah card number sudah tersedia di database
        if (cardRepository.findByNumber(cardNumber).isPresent()){
            if (cardRepository.existsByNumberAndIsLockedTrue(cardNumber)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Card Number yang ditandai telah dicuri");
            } else {
                // lock card yang ada untuk mencegah upaya penipuan
                card = cardRepository.findByNumber(cardNumber).get();
                card.setLocked(true);
            }
        } else {
            // create new entity dan lock
            card = new Card(cardNumber, true);
        }
        cardRepository.save(card);
        return card;
    }

    // remove card yang berpotensi tercuri dari database
    public String deleteStolenCard(String cardNumber){
        if (CardValidator.isNonValid(cardNumber)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Card Number");
        }

        Card card = cardRepository.findByNumber(cardNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card Number Not Found"));

        if (card.isLocked()) cardRepository.delete(card);

        return "Card " + cardNumber + " successfully removed!";
    }

    // return list semua card yang berpotensi tercuri
    public List<Card> geStolenCard(){
        return cardRepository.findAllByIsLockedTrue();
    }
}
