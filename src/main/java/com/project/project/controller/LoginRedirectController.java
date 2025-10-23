package com.project.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginRedirectController {

    // Accept any method to /login and redirect to the static login page
    @RequestMapping("/login")
    public String redirectToLoginHtml() {
        return "redirect:/login.html";
    }
}
