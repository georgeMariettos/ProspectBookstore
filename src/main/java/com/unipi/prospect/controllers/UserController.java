package com.unipi.prospect.controllers;

import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.users.Admin;
import com.unipi.prospect.users.Author;
import com.unipi.prospect.users.Customer;
import com.unipi.prospect.users.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    
    private final UserDAO userDAO = new UserDAO();
    
    @Autowired
    private CustomerController customerController;

    @Autowired
    private AuthorController authorController;

    @Autowired
    private AdminController adminController;

    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        HttpSession session) {
        
        // Try to authenticate as admin first
        User authenticatedUser = userDAO.authenticate(username, password, "Admin");
        
        // If not admin, try author
        if (authenticatedUser == null) {
            authenticatedUser = userDAO.authenticate(username, password, "Author");
        }
        
        // If not author, try customer
        if (authenticatedUser == null) {
            authenticatedUser = userDAO.authenticate(username, password, "Customer");
        }
        
        // If authentication failed for all roles
        if (authenticatedUser == null) {
            return "redirect:/login.html?error=true";
        }
        
        // Store user in session
        session.setAttribute("user", authenticatedUser);
        session.setAttribute("username", authenticatedUser.getUsername());
        
        // Store additional customer information if applicable
        if (authenticatedUser instanceof Customer customer) {
            session.setAttribute("customerAddress", customer.getAddress());
        }
        
        // Redirect based on user role
        if (authenticatedUser instanceof Admin) {
            return "redirect:/admin/main";
        } else if (authenticatedUser instanceof Author) {
            return "redirect:/author";
        } else if (authenticatedUser instanceof Customer) {
            return "redirect:/customer";
        } else {
            // Fallback (shouldn't happen with proper user type checking)
            return "redirect:/";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login.html";
    }
}
