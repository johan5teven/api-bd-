package com.project.project.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestErrorController {

    @GetMapping("/error")
    public String throwError() {
        throw new RuntimeException("test error");
    }
}
