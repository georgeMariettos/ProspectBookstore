package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.Order;
import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.db.commerce.OrderDao;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.product.Item;
import com.unipi.prospect.users.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    UserDAO userDAO = new UserDAO();
    @PostMapping("/register")
    public String registerCustomer(@RequestParam(name = "username") String username,
                                   @RequestParam(name = "name") String name,
                                   @RequestParam(name = "surname") String surname,
                                   @RequestParam(name = "address") String address,
                                   @RequestParam(name = "password") String password,
                                   @RequestParam(name = "confirmPassword") String conPass) {
        if (new UserDAO().selectByUsername(username, "Customer") != null){
            return "redirect:/registration.html?error=username+already+exists";
        }
        try{
            new UserDAO().insert(new Customer(username, password, name, surname, true, address));
            return "redirect:/login.html";
        }catch(Exception e){
            return "redirect:/registration.html?error=There+was+an+error+registering+your+account.+Try+again+later";
        }
    }

    // Login is now handled by the main UserController

    @GetMapping
    public String CustomerPage(Model model, HttpSession session){
        // Get the logged-in username from session
        String username = (String) session.getAttribute("username");

        // If not logged in, redirect to login page
        if (username == null) {
            return "redirect:/login.html";
        }
        // Fetch orders for this customer
        OrderDao orderDao = new OrderDao();
        ArrayList<Order> orders = orderDao.selectAllByUsername(username);

        ArrayList<Book> books = new ArrayList<Book>();
        // Add orders to the model
        model.addAttribute("orders", orders);
        for (Order order : orders) {
            for (Item item : order.getItems()) {
                books.add(new BookDao().selectByIsbn(item.getIsbn()));
            }
        }
        model.addAttribute("books", books);
        model.addAttribute("username", username);

        return "customer";
    }
}

