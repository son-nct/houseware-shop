package com.example.shop.repository;

import com.example.shop.entity.StatusAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusAccountRepository extends JpaRepository<StatusAccount,Integer> {
}
