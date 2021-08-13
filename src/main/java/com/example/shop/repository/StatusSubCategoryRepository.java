package com.example.shop.repository;

import com.example.shop.entity.StatusSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusSubCategoryRepository extends JpaRepository<StatusSubCategory,Integer> {
}
