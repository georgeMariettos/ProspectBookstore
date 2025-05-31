package com.unipi.prospect.controllers;

import com.unipi.prospect.users.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/customer")
public class CustomerController {


    @PostMapping("/register")
    public String registerCustomer(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String name,
                                   @RequestParam String address) {


        //Customer customer = new Customer(username, password, name, address);

        return "redirect:/index.html";
    }

    @GetMapping
    public String CustomerPage(){
        return "customer";
    }

}
