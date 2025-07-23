package edu.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String welcomePage() {
        return "index";
    }

    @GetMapping("/success")
    public String successPage() {
        return "success";
    }

}
