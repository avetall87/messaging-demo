package ru.spb.avetall.messagingdemo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

    @RequestMapping({"","/","index","index.html"})
    public String entryPoint(){
        return "index";
    }

}
