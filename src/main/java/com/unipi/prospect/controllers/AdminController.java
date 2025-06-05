package com.unipi.prospect.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public String AdminPage(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        username = "temp";
        if (username == null) {
            return "redirect:/";
        }
        model.addAttribute("username", username);
        return "admin";
    }

}
