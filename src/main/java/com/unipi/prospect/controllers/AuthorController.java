package com.unipi.prospect.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/author")
public class AuthorController {

    @GetMapping
    public String AuthorPage(){
        return "redirect:/author.html";
    }

}

