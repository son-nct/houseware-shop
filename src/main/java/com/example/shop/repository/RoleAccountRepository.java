package com.example.shop.repository;

import com.example.shop.entity.RoleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAccountRepository extends JpaRepository<RoleAccount,Integer> {
}
