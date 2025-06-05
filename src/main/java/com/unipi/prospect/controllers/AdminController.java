package com.unipi.prospect.controllers;

import com.unipi.prospect.db.commerce.OrderDao;
import com.unipi.prospect.db.communication.TicketDao;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.users.Admin;
import com.unipi.prospect.users.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/main")
    public String adminMainPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }else if (!(user instanceof Admin)){
            return "redirect:/";
        }
        model.addAttribute("username", user.getUsername());

        String tempStr;

        try {
            tempStr = Integer.toString(new BookDao().getCountOfBooks());
        }catch (Exception e){
            tempStr = "Error getting count of books";
        }
        model.addAttribute("books", tempStr);

        try {
            tempStr = Integer.toString(new TicketDao().getCountOfTicketsByStatus("opened"));
        }catch (Exception e){
            tempStr = "Error getting count of tickets";
        }
        model.addAttribute("tickets", tempStr);

        try {
            tempStr = Integer.toString(new OrderDao().getCountOfOrdersByStatus("preparing"));
        }catch (Exception e){
            tempStr = "Error getting count of orders";
        }
        model.addAttribute("orders", tempStr);

        try {
            tempStr = Integer.toString(new UserDAO().getCountOfUsers());
        }catch (Exception e){
            tempStr = "Error getting count of Users";
        }
        model.addAttribute("users", tempStr);

        return "admin";
    }

    @GetMapping("/products")
    public String adminProductsPage(Model model, HttpSession session) {
        return "adminProducts";
    }

    @GetMapping("/orders")
    public String adminOrdersPage(Model model, HttpSession session) {
        return "adminOrders";
    }

    @GetMapping("/tickets")
    public String adminTicketsPage(Model model, HttpSession session) {
        return "adminTickets";
    }

    @GetMapping("/users")
    public String adminUsersPage(Model model, HttpSession session) {
        return "adminUsers";
    }

}
