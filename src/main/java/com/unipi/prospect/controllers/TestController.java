package com.unipi.prospect.controllers;

import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.users.Admin;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(HttpSession session){
        session.invalidate(); //did this to log out because logging out isn't implemented yet
        return "redirect:/index.html";
    }
}
