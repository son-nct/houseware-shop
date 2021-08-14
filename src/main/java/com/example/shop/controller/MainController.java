package com.example.shop.controller;

import com.example.shop.entity.Cart;
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
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
    public String detail(Model model, @RequestParam("productId") Long productId) {
        Product product = productRepository.getProductById(productId);
        Image image = new Image();
        image.setImageUrl(product.getImageUrl());

        List<Image> images = imageRepository.getImageByProductId(productId);
        images.add(0, image);
        model.addAttribute("images", images);
        model.addAttribute("product", product);
        model.addAttribute("totalImg", images.size());

        return "detail";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(Model model, HttpSession session, @RequestParam("productId") Long productId) {
        Product product = productRepository.getProductById(productId);

        //để dùng dc session thì gọi đến httpSession
        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setProductCode(product.getCode());
        cart.setProductName(product.getName());
        cart.setProductQuantity(product.getQuantity());
        cart.setProductPrice(product.getPrice());
        cart.setProductDescription(product.getDescription());
        cart.setProductImageUrl(product.getImageUrl());
        cart.setQuantity(1);

        List<Cart> listCart = (List<Cart>) session.getAttribute("CART");

        if (listCart == null) {
            listCart = new ArrayList<>();
            listCart.add(cart);
        } else {
            boolean isExist = false;
            for (Cart c : listCart) {
                if (c.getProductId().equals(productId)) {
                    isExist = true;
                    c.setQuantity(c.getQuantity() + 1);
                }

            }
            if (!isExist) {
                listCart.add(cart);
            }

        }
        session.setAttribute("CART", listCart);
        return "redirect:/carts";
    }

    @GetMapping("/carts")
    public String listCart(HttpSession session, Model model) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("CART");
        if (listCart == null || listCart.size() == 0) {
            return "emptyCart";
        } else {
            double totalMoney = 0;
            for (Cart cart : listCart) {
                totalMoney += cart.getQuantity() * cart.getProductPrice();
            }
            totalMoney = Math.ceil(totalMoney * 100) / 100;

            model.addAttribute("totalMoney", totalMoney);
            model.addAttribute("listCart", listCart);
            return "listCart";
        }
    }

    @GetMapping("/deleteCart")
    public String deleteProduct(HttpSession session, @RequestParam("productId") Long productId) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("CART");

        for (Cart cart : listCart) {
            if (cart.getProductId().equals(productId)) {
                listCart.remove(cart);
                break;
            }
        }


        session.setAttribute("CART", listCart);

        if (listCart.size() > 0) {
            return "redirect:/carts";
        } else {
            return "emptyCart";
        }
    }

    @GetMapping("/deleteAllCart")
    public String deleteAllCart(HttpSession session) {
        session.removeAttribute("CART");
        return "redirect:/carts";
    }

    @GetMapping("/search")
    public String searchProduct(Model model,
                                @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                @RequestParam(value = "page", defaultValue = "1") int page) {
        final int PAGE_SIZE = 20;
        Page<Product> products = productRepository.searchPagination(keyword, PageRequest.of(page - 1, PAGE_SIZE));

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("curPage", page);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "listProduct";

    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("CART");
        double totalMoney = 0;
        for (Cart cart : listCart) {
            totalMoney += cart.getQuantity() * cart.getProductPrice();
        }
        totalMoney = Math.ceil(totalMoney * 100) / 100;

        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("listCart", listCart);
        return "checkout";
    }

    @GetMapping("/prepare-shipping")
    public String prepareShipping() {
        return "prepareShipping";
    }
}
