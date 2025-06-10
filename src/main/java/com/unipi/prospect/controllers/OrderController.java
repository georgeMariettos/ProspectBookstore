package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.*;
import com.unipi.prospect.db.commerce.OrderDao;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.product.Item;
import com.unipi.prospect.users.Customer;
import com.unipi.prospect.users.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.ArrayList;

@Controller
@RequestMapping("/order")
public class OrderController {
    @GetMapping
    public String getOrderForm(HttpSession session, Model model){
        if(session.getAttribute("username") != null){
            if(session.getAttribute("role").equals("Customer")){
                ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("cart");
                model.addAttribute("total", shoppingCart.getTotal());
                return "order";
            }
        }
        return "redirect:/";
    }

    @PostMapping("/confirmPayment")
    public String confirmPayment(HttpSession session,
                                 @RequestParam(name = "cardNumber") String cardNumber,
                                 @RequestParam(name = "security") String CVV,
                                 @RequestParam(name = "name") String name){
        if(session.getAttribute("username") != null){
            if(session.getAttribute("role").equals("Customer")){
                ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("cart");
                Card card = new Card(shoppingCart.getTotal(), "Visa", "02/27", cardNumber, name, CVV);
                Payment payment = new Payment(shoppingCart.getTotal());
                //Call to outside payment system
                boolean success = card.Authorize();
                payment.pay(card);
                return "redirect:/order/complete?success="+success;
            }
        }
        throw new RuntimeException("User is not logged in or is not a customer");
    }

    @GetMapping("/confirmPayment")
    public String confirmPayment(HttpSession session){
        if(session.getAttribute("username") != null){
            if(session.getAttribute("role").equals("Customer")){
                ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("cart");
                IPayment cash = new Cash(shoppingCart.getTotal());
                Payment payment = new Payment(shoppingCart.getTotal());
                payment.pay(cash);
                return "redirect:/order/complete?success=true";
            }
        }
        throw new RuntimeException("User is not logged in or is not a customer");
    }

    @GetMapping("/complete")
    public String complete(HttpSession session, @RequestParam(name = "success") boolean success){
        if(session.getAttribute("username") != null){
            if(session.getAttribute("role").equals("Customer")){
                Customer customer = (Customer)session.getAttribute("user");
                ShoppingCart shoppingCart = (ShoppingCart) session.getAttribute("cart");
                for (Item item : shoppingCart.getItems()){
                    Book book = new BookDao().selectByIsbn(item.getIsbn());
                    book.setStock(book.getStock() - item.getQuantity());
                    new BookDao().update(book);
                }
                new OrderDao().insert(new Order(new Date(System.currentTimeMillis()), customer.getAddress(), customer.getUsername(), shoppingCart.getItems()));
                session.setAttribute("cart", new ShoppingCart(new Date(System.currentTimeMillis()), new ArrayList<Item>()));
                return "orderComplete";
            }
        }
        throw new RuntimeException("User is not logged in or is not a customer");
    }
}
