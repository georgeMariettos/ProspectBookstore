package com.unipi.prospect.controllers;

import com.unipi.prospect.db.users.UserDAO;
import com.unipi.prospect.users.Admin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String test(){
        Admin admin = new Admin("admin", "123", "a", "b", true);
        System.out.println(new UserDAO().insert(admin));
        return "redirect:/index.html";
    }
}
