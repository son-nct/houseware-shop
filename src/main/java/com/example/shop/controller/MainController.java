package com.example.shop.controller;

import com.example.shop.entity.*;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ImageRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.ShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.GeneratedValue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class MainController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ShippingRepository shippingRepository;

    @GetMapping("/")
    public String home(Model model) {
        int count = 0;

        String random = "";

        List<Product>listRandom = new ArrayList<>();

        while(count < 12) {
            random = ThreadLocalRandom.current().nextInt(1,189)+"";
            Product product = productRepository.getProductById(Long.parseLong(random));
            listRandom.add(product);
            count++;
        }

        model.addAttribute("listRandom",listRandom);

        Page<Product>measureProducts = productRepository.findByCategoryId(2,PageRequest.of(0,12));
        Page<Product>homeGardenProducts =  productRepository.findByCategoryId(5,PageRequest.of(0,12));
        Page<Product>toolProducts =  productRepository.findByCategoryId(1,PageRequest.of(0,12));

        model.addAttribute("measureProducts",measureProducts);
        model.addAttribute("homeGardenProducts",homeGardenProducts);
        model.addAttribute("toolProducts",toolProducts);
        return "home";
    }


    @GetMapping("/products")
    public String listProduct(HttpSession session, Model model, @RequestParam(name = "page", defaultValue = "1") int page,
                              @RequestParam(name = "categoryId", defaultValue = "-1") int categoryId,
                              @RequestParam(name = "subCategoryId", defaultValue = "-1") int subCategoryId) {


        final int PAGE_SIZE = 20;
        //pageable index từ 0
        Page<Product> products = productRepository.findAll(PageRequest.of(page - 1, PAGE_SIZE));

        model.addAttribute("noCategory", "true");
        model.addAttribute("noFilter", "true");

        if (subCategoryId != -1) {

            products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0, PAGE_SIZE));


            //khi xài page ==> để lấy dc size page ==> objPage.getContent().size()
            if (products.getContent().size() == 0) {
                products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0, PAGE_SIZE));
                page = 1;
            }


        } else if (categoryId != -1) {

            model.addAttribute("noCategory", "false");

            String checkExistCategoryId = (String) session.getAttribute("oldCategoryId");
            if (checkExistCategoryId == null) {
                session.setAttribute("oldCategoryId", "-1");
            } else {
                int oldCategoryId = Integer.parseInt(checkExistCategoryId);
                if (oldCategoryId != categoryId) {
                    products = productRepository.findByCategoryId(categoryId, PageRequest.of(0, PAGE_SIZE));


                } else {
                    products = productRepository.findByCategoryId(categoryId, PageRequest.of(page - 1, PAGE_SIZE));
                }

                if (products.getContent().size() == 0) {
                    products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0, PAGE_SIZE));
                    page = 1;
                }
                session.setAttribute("oldCategoryId", categoryId + "");
            }


            model.addAttribute("categoryId", categoryId);


            //khi xài page ==> để lấy dc size page ==> objPage.getContent().size()


        }
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("curPage", page);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("keyword", "");
        return "listProduct";
    }


    @GetMapping("/filter")
    public String filterByPrice(Model model, @RequestParam(value = "cboPrice", defaultValue = "0") int filterId,
                                @RequestParam(value = "page", defaultValue = "1") int page) {

        final int PAGE_SIZE = 20;

        model.addAttribute("noCategory", "true");
        model.addAttribute("noFilter", "false");

        Page<Product> products = productRepository.findAll(PageRequest.of(page - 1, PAGE_SIZE));


        switch (filterId) {
            case 0:
                products = productRepository.findAll(PageRequest.of(page - 1, PAGE_SIZE));

                if (products.getContent().size() == 0) {
                    products = productRepository.findAll(PageRequest.of(0, PAGE_SIZE));
                    page = 1;
                }
                break;
            case 1:
                products = productRepository.filterProductByPriceBetween(1, 5, PageRequest.of(page - 1, PAGE_SIZE));

                if (products.getContent().size() == 0) {
                    products = productRepository.filterProductByPriceBetween(1, 5, PageRequest.of(0, PAGE_SIZE));
                    page = 1;
                }
                break;
            case 2:
                products = productRepository.filterProductByPriceBetween(5, 10, PageRequest.of(page - 1, PAGE_SIZE));

                if (products.getContent().size() == 0) {
                    products = productRepository.filterProductByPriceBetween(5, 10, PageRequest.of(0, PAGE_SIZE));
                    page = 1;
                }
                break;
            case 3:
                products = productRepository.filterProductByPriceBetween(10, 20, PageRequest.of(page - 1, PAGE_SIZE));

                if (products.getContent().size() == 0) {
                    products = productRepository.filterProductByPriceBetween(10, 20, PageRequest.of(0, PAGE_SIZE));
                    page = 1;
                }
                break;
            case 4:
                products = productRepository.getProductByPriceGreaterThanEqual(20, PageRequest.of(page - 1, PAGE_SIZE));

                if (products.getContent().size() == 0) {
                    products = productRepository.getProductByPriceGreaterThanEqual(20, PageRequest.of(0, PAGE_SIZE));
                    page = 1;
                }
                break;
        }


        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("curPage", page);
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("keyword", "");
        model.addAttribute("filterId", filterId);


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


    @GetMapping("/add-to-cart-homepage")
    public String addToCartHomePage(Model model, HttpSession session, @RequestParam("productId") Long productId) {
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
        return "redirect:/";
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
        model.addAttribute("noFilter", "true");

        if (products.getContent().size() == 0) {
            products = productRepository.searchPagination(keyword, PageRequest.of(0, PAGE_SIZE));
            page = 1;
        }

        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("curPage", page);
        model.addAttribute("page", page);

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("totalPage", products.getTotalPages());
        model.addAttribute("keyword", keyword);

        if(keyword.equals("")) {
            return "redirect:/products";
        }
        return "listProduct";

    }

    @GetMapping("/update-quantity")
    public String updateQuantity(Model model, HttpSession session, @RequestParam("quantity") Integer quantity,
                                 @RequestParam("productId") Long productId) {
        List<Cart> cartList = (List<Cart>) session.getAttribute("CART");
        for (Cart cart : cartList) {
            if (cart.getProductId().equals(productId)) {
                cart.setQuantity(quantity);
                break;
            }
        }

        session.setAttribute("CART", cartList);
        return "redirect:/carts";
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
    public String prepareShipping(HttpSession session, Model model, @RequestParam("name") String fullName,
                                  @RequestParam("phone") String phone,
                                  @RequestParam("address") String address,
                                  @RequestParam("note") String note) {
        Shipping shipping = new Shipping();
        shipping.setName(fullName);
        shipping.setPhone(phone);
        shipping.setAddress(address);
        model.addAttribute("note", note);

        session.setAttribute("SHIPPING", shipping);

        List<Cart> cartList = (List<Cart>) session.getAttribute("CART");
        double totalMoney = 0;
        for (Cart cart : cartList) {
            totalMoney += cart.getQuantity() * cart.getProductPrice();
        }

        totalMoney = Math.ceil(totalMoney * 100) / 100;
        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("shipping", shipping);
        model.addAttribute("cartList", cartList);

        return "prepareShipping";
    }

    @GetMapping("/thanks")
    public String thanks(HttpSession session) {
//        Shipping shipping = (Shipping) session.getAttribute("SHIPPING");
//        shippingRepository.save(shipping);
        return "thanks";
    }
}
