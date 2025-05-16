package com.kh.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index() {
        System.out.println("Đẹp trai nhỉ?");
        return "index";
    }
}
