package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.Order;
import com.unipi.prospect.db.commerce.OrderDao;
import com.unipi.prospect.db.users.UserDAO;
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
    public String registerCustomer(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String name,
                                   @RequestParam String address) {

        //Customer customer = new Customer(username, password, name, address);

        return "redirect:/index.html";
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

        // Add orders to the model
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);

        return "customer";
    }
}

