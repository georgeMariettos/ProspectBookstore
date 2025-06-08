package com.unipi.prospect.controllers;

import com.unipi.prospect.db.product.BookDao;
import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.product.Book;
import com.unipi.prospect.users.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(HttpSession session){
        session.invalidate(); //did this to log out because logging out isn't implemented yet
        try{
            new BookDao().insert(new Book("1", "testing", "author1", "test", "test", "test", "test", 2, "test", "test", 0.11f, 1));
        }catch(Exception e){
            System.out.println(e);
        }
        return "redirect:/index.html";
    }

    @ResponseBody
    @GetMapping("/t")
    public boolean test2(HttpSession session){
        return true;
    }
}
