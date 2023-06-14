package com.novianto.antifraud.system.repository;

import com.novianto.antifraud.system.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNumber(String ip);

    List<Card> findAllByIsLockedTrue();

    boolean existsByNumberAndIsLockedTrue(String number);
}
