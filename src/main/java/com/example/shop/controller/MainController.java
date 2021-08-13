package com.example.shop.controller;

import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.GeneratedValue;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/products")
    public String listProduct(Model model, @RequestParam(name = "page",defaultValue = "1")Integer page) {
        final int pageSize = 20;
        //pageable index từ 0
        Page<Product>products = productRepository.findAll(PageRequest.of(page,pageSize));
        List<Category>categories = categoryRepository.findAll();

        model.addAttribute("products",products);
        model.addAttribute("categories",categories);
        model.addAttribute("totalPage",products.getTotalPages());
        return "listProduct";
    }

    @GetMapping("/detail")
    public String detail() {
        return "detail";
    }

    @GetMapping("/carts")
    public String listCart() {
        return "listCart";
    }

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @GetMapping("/prepare-shipping")
    public String prepareShipping() {
        return "prepareShipping";
    }
}
