package com.example.shop.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false,unique = true)
    private String username;
    private String password;
    private String displayName;
    private String address;
    private String email;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleAccount role;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusAccount status;

    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    private List<Order> orders;
}
