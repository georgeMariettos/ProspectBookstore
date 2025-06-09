package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.ShoppingCart;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.product.Item;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Date;
import java.util.ArrayList;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {
    @GetMapping
    String getShoppingCart(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            if (session.getAttribute("role").equals("Customer")) {
                ShoppingCart shoppingCart = (ShoppingCart) (session.getAttribute("cart"));
                model.addAttribute("cartItems", shoppingCart.getItems());
                model.addAttribute("date", shoppingCart.getCreated());
            }else {
                return "redirect:/";
            }
        }else{
            return "redirect:/login";
        }
        return "cart";
    }

    @ResponseBody
    @GetMapping("/add")
    public boolean addToCart(HttpSession session, @RequestParam(name = "isbn") String isbn) {
        if (session.getAttribute("role").equals("Customer")) {
            try{
                ShoppingCart shoppingCart = (ShoppingCart) (session.getAttribute("cart"));
                ArrayList<Item> items = shoppingCart.getItems();
                Book book = new BookDao().selectByIsbn(isbn);
                items.add(new Item(book));
                shoppingCart.setItems(items);
                return true;
            }catch(Exception e){
                return false;
            }
        }else{
            return false;
        }
    }
}
