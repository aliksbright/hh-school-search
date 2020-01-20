package ru.suchkov.search.controller;

import org.springframework.stereotype.Controller;

@Controller
public class ApplicationController {

    public void start(String... args) {
        System.out.println("App start with argument : " + args[0]);
    }
}
