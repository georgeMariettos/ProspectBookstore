package com.unipi.prospect.controllers;

import com.unipi.prospect.commerce.Order;
import com.unipi.prospect.communication.Ticket;
import com.unipi.prospect.db.DBConnection;
import com.unipi.prospect.db.commerce.OrderDao;
import com.unipi.prospect.db.communication.TicketDao;
import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.users.Admin;
import com.unipi.prospect.users.Author;
import com.unipi.prospect.users.Customer;
import com.unipi.prospect.users.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/main")
    public String adminMainPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (checkInvalidSession(session)) {
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
        if (checkInvalidSession(session)) {
            return "redirect:/";
        }
        ArrayList<Book> books;
        books = new BookDao().selectAll();
        model.addAttribute("books", books);
        return "adminProducts";
    }

    @GetMapping("/orders")
    public String adminOrdersPage(Model model, HttpSession session, @RequestParam(required = false, name = "tab") String tab) {
        if (checkInvalidSession(session)) {
            return "redirect:/";
        }
        ArrayList<Order> orders;
        if (tab == null){
            tab = "preparing";
        }
        orders = new OrderDao().selectAllByStatus(tab);
        model.addAttribute("orders", orders);
        model.addAttribute("tab", tab);
        return "adminOrders";
    }

    @ResponseBody
    @GetMapping("/orders/confirm")
    public boolean adminOrdersConfirmPage(HttpSession session, @RequestParam(required = false, name = "orderID") String orderID) {
        if (checkInvalidSession(session)) {
            throw new RuntimeException("Invalid session");
        }
        try {
            Order order = new OrderDao().selectByOrderID(Integer.parseInt(orderID));
            order.setStatus("sent");
            new OrderDao().update(order);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @GetMapping("/tickets")
    public String adminTicketsPage(Model model, HttpSession session, @RequestParam(required = false, name = "tab") String tab) {
        if (checkInvalidSession(session)) {
            return "redirect:/";
        }
        ArrayList<Ticket> tickets;
        if (tab == null){
            tab = "opened";
        }
        tickets = new TicketDao().selectAllTicketsByStatus(tab);
        model.addAttribute("orders", tickets);
        model.addAttribute("tab", tab);
        return "adminTickets";
    }

    @GetMapping("/users")
    public String adminUsersPage(Model model, HttpSession session, @RequestParam(required = false, name = "tab") String tab) {
        if (checkInvalidSession(session)) {
            return "redirect:/";
        }
        ArrayList<User> users;
        if (tab == null){
            tab = "Customer";
        }
        users = new UserDAO().selectAll(tab);
        model.addAttribute("users", users);
        model.addAttribute("tab", tab);
        return "adminUsers";
    }

    @GetMapping("/users/edit")
    public String adminUsersEditPage(Model model,@RequestParam(required = false, name = "username") String username, HttpSession session, @RequestParam(required = false, name = "role") String role) {
        if (checkInvalidSession(session)) {
            return "redirect:/";
        }
        User user = new UserDAO().selectByUsername(username, role);
        model.addAttribute("user", user);
        String address = null;
        if (user instanceof Customer) {
            address = new UserDAO().selectAddressByUsername(username);
        }
        model.addAttribute("address", address);
        model.addAttribute("role", role);
        return "adminEditUser";
    }

    @GetMapping("/users/new")
    public String adminUsersNewPage(HttpSession session) {
        if (checkInvalidSession(session)) {
            return "redirect:/";
        }
        return "adminAddUser";
    }

    @PostMapping("/users/update")
    public String adminUpdateUser(@RequestParam("username") String username,
                                @RequestParam("name") String name,
                                @RequestParam("surname") String surname,
                                @RequestParam("address") String address,
                                @RequestParam("email")  String email,
                                @RequestParam("active") String active,
                                @RequestParam("role") String role,
                                HttpSession session) {
        try{
            if (checkInvalidSession(session)) {
                return "redirect:/";
            }
            User user;
            if (role.equals("Customer")) {
                Customer customer = (Customer) (new UserDAO().selectByUsername(username, role));
                customer.setUsername(username);
                customer.setName(name);
                customer.setSurname(surname);
                customer.setAddress(address);
                customer.setActive(active.equals("on"));
                user = customer;
            }else if (role.equals("Admin")) {
                Admin admin = (Admin) (new UserDAO().selectByUsername(username, role));
                admin.setUsername(username);
                admin.setName(name);
                admin.setSurname(surname);
                admin.setActive(active.equals("on"));
                user = admin;
            }else{
                Author author = (Author) (new UserDAO().selectByUsername(username, role));
                author.setUsername(username);
                author.setName(name);
                author.setSurname(surname);
                author.setActive(active.equals("on"));
                user = author;
            }
            new UserDAO().update(user);
            return "redirect:/admin/users?tab=" + role;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/users/save")
    public String adminSaveUser(@RequestParam("username") String username,
                                @RequestParam("password") String password,
                                @RequestParam("name") String name,
                                @RequestParam("surname") String surname,
                                @RequestParam("address") String address,
                                @RequestParam("email")  String email,
                                @RequestParam("active") String active,
                                @RequestParam("role") String role,
                                HttpSession session){
        try{
            if (checkInvalidSession(session)) {
                return "redirect:/";
            }
            User user;
            boolean temp = active.equals("on");
            if (role.equals("Customer")) {
                user = new Customer(username, password, name, surname, temp, address);
            }else if (role.equals("Admin")) {
                user = new Admin(username, password, name, surname, temp);
            }else{
                user = new Author(username, password, name, surname, temp);
            }
            new UserDAO().insert(user);
            return "redirect:/admin/users?tab=" + role;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkInvalidSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null){
            return true;
        } else {
            return !(user instanceof Admin);
        }
    }
}
