package com.example.shop.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString
public class StatusCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String status;

    @OneToMany(mappedBy = "status",cascade = CascadeType.ALL)
    private List<Category> categories;

}
