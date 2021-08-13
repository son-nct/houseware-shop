package com.example.shop.repository;

import com.example.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    //p.subCategory tương đương Subcategory
    //trong subcategory có đối tượng category ==> để lấy dc categoryid ==> sub.category.id
    //muốn có phân trang thì dùng countQuery
    @Query(value = "select p from Product  p inner join p.subCategory sub where sub.category.id = ?1",
    countQuery = "select count (p) from Product p inner join p.subCategory sub where sub.category.id = ?1")
    public Page<Product> findByCategoryId(int categoryId, Pageable pageable);

    public Page <Product> findBySubCategoryId(int subCategoryId, Pageable pageable);
}
