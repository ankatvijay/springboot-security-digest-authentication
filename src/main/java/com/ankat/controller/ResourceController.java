package com.ankat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/hello")
    public String defaultPage(){
        return "Hello World";
    }
}
