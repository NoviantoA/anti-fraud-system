package com.novianto.antifraud.system.repository;

import com.novianto.antifraud.system.entity.IPAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspiciousIPRepository extends JpaRepository<IPAddress, Long> {

    Optional<IPAddress> findByIp(String ip);
}
