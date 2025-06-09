package com.unipi.prospect.controllers;

import com.unipi.prospect.db.product.BookDao;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/author")
public class AuthorController {

    @GetMapping
    public String authorPage(HttpSession session, Model model) {
        Object username = session.getAttribute("username");

        if (username == null) {
            return "redirect:/login.html";
        }

        model.addAttribute("username", username);

        return "author";
    }

}

