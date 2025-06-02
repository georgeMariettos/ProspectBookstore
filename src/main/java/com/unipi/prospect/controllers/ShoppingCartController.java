package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.ShoppingCart;
import com.unipi.prospect.product.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.util.ArrayList;

@Controller
public class ShoppingCartController {
    @GetMapping("/cart")
    String getShoppingCart(Model model) {
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item("0", 1, 5.99f));
        items.add(new Item("1", 2, 11.99f));
        ShoppingCart shoppingCart = new ShoppingCart(new Date(System.currentTimeMillis()), items);
        model.addAttribute("title", "Your Cart");
        model.addAttribute("cartItems", shoppingCart.getItems());
        model.addAttribute("date", shoppingCart.getCreated());
        return "cart";
    }
}
