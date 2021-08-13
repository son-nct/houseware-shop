package com.example.shop.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Cart {
    private Long productId;
    private String productCode;
    private String productName;
    private int productQuantity;
    private double productPrice;
    private String productDescription;
    private String productImageUrl;

    private int quantity;
}
