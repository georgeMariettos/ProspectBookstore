package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.ShoppingCart;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.product.Item;
import jakarta.mail.Session;
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
    public String getShoppingCart(Model model, HttpSession session) {
        if (session.getAttribute("username") != null) {
            if (session.getAttribute("role").equals("Customer")) {
                ShoppingCart shoppingCart = (ShoppingCart) (session.getAttribute("cart"));
                ArrayList<Book> books = new ArrayList<Book>();
                for (Item item : shoppingCart.getItems()) {
                    books.add(new BookDao().selectByIsbn(item.getIsbn()));
                }
                model.addAttribute("books", books);
                model.addAttribute("cart", shoppingCart);
                model.addAttribute("date", shoppingCart.getCreated());
            }else {
                return "redirect:/";
            }
        }else{
            return "redirect:/";
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

    @GetMapping("/setQ")
    public String setQ(HttpSession session,
                        @RequestParam(name = "op") String op,
                        @RequestParam(name = "isbn") String isbn,
                        @RequestParam(name = "stock") String stock) {

        if (session.getAttribute("role").equals("Customer")) {
            ShoppingCart shoppingCart = (ShoppingCart) (session.getAttribute("cart"));
            int intStock = Integer.parseInt(stock);
            for(Item item : shoppingCart.getItems()){
                if (isbn.equals(item.getIsbn())) {
                    if (op.equals("plus") && intStock >= item.getQuantity() + 1) {
                        item.setQuantity(item.getQuantity() + 1);
                        shoppingCart.calculateTotal();
                        return "redirect:/cart";
                    }else if (op.equals("minus") && 0 < item.getQuantity() - 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        shoppingCart.calculateTotal();
                        return "redirect:/cart";
                    }
                }
            }
            return "redirect:/cart";
        }else {
            return "redirect:/";
        }
    }

    @GetMapping("/clear")
    public String clear(HttpSession session) {
        if (session.getAttribute("role").equals("Customer")) {
            session.setAttribute("cart", new ShoppingCart(new Date(System.currentTimeMillis()), new ArrayList<Item>()));
            return "redirect:/cart";
        }else{
            return "redirect:/";
        }
    }
}
