package com.novianto.antifraud.system.repository;

import com.novianto.antifraud.system.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Optional<User> findUserByUsername(String username);

    List<User> findAll();
}
