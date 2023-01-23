package com.softavail.examination;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping(path = "/", produces = "text/html")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Vehicle Status");
        return "home";
    }
}
