package com.churchevents.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @GetMapping("/AboutUs")
    public String getAboutUs(Model model){
        model.addAttribute("bodyContent", "aboutUs");
        return "master-template";
    }

    @GetMapping("/Pastoren")
    public String getPastoren(Model model){
        model.addAttribute("bodyContent", "pastoren");
        return "master-template";
    }

}
