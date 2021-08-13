package com.example.shop.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String imageUrl;
    //    private int status; // 0-1
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
