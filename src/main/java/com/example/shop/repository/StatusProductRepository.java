package com.example.shop.repository;

import com.example.shop.entity.StatusProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusProductRepository extends JpaRepository<StatusProduct,Integer> {
}
