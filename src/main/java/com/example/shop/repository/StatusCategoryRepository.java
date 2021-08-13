package com.example.shop.repository;

import com.example.shop.entity.StatusCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusCategoryRepository extends JpaRepository<StatusCategory,Integer> {
}
