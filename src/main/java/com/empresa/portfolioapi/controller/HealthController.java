package com.empresa.portfolioapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")

public class HealthController{

    @GetMapping
    public String chek(){
        return "API do portgilio esta funcionando.";
    }
}