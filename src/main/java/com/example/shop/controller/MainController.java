package com.example.shop.controller;

import com.example.shop.entity.Category;
import com.example.shop.entity.Image;
import com.example.shop.entity.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ImageRepository;
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
    ImageRepository imageRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/products")
    public String listProduct(Model model, @RequestParam(name = "page", defaultValue = "1") Integer page,
                              @RequestParam(name = "categoryId", defaultValue = "-1") Integer categoryId,
                              @RequestParam(name = "subCategoryId", defaultValue = "-1") Integer subCategoryId) {
        final int pageSize = 20;
        //pageable index từ 0
        Page<Product> products;


        if (subCategoryId != -1) {
            products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(page - 1, pageSize));

            //khi xài page ==> để lấy dc size page ==> objPage.getContent().size()
            if (products.getContent().size() == 0) {
                products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0, pageSize));
                page = 1;
            }

        } else if (categoryId != -1) {
            products = productRepository.findByCategoryId(categoryId, PageRequest.of(page - 1, pageSize));

            //khi xài page ==> để lấy dc size page ==> objPage.getContent().size()
            if (products.getContent().size() == 0) {
                products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0, pageSize));
                page = 1;
            }

        } else {
            products = productRepository.findAll(PageRequest.of(page - 1, pageSize));
        }
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("curPage", page);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalPage", products.getTotalPages());
        return "listProduct";
    }

    @GetMapping("/detail")
    public String detail(Model model, @RequestParam("productId")Long productId) {
        Product product = productRepository.getProductById(productId);
        List<Image>images = imageRepository.getImageByProductId(productId);

        model.addAttribute("images",images);
        model.addAttribute("mainImg",product.getImageUrl());
        model.addAttribute("product",product);
        model.addAttribute("totalImg",images.size());

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
