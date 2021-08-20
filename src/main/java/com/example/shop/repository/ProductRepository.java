package com.example.shop.repository;

import com.example.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    public Product getProductById(Long id);


    @Query(value = "SELECT p FROM Product  p WHERE p.price BETWEEN ?1 AND ?2",
    countQuery = "select count (p) FROM Product  p WHERE p.price BETWEEN ?1 AND ?2")
    public Page<Product>filterProductByPriceBetween(double price, double price2, Pageable pageable);


    public Page<Product>getProductByPriceGreaterThanEqual(double price, Pageable pageable);


    @Query(value = "SELECT p from Product p where concat(p.name, p.description) like %?1%",
            countQuery = "SELECT count (p) from Product p where concat(p.name, p.description) like %?1%")
    public Page<Product> searchPagination(String keyword, Pageable pageable);
}
