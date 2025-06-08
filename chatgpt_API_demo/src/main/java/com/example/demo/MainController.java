package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private final ConnectChatgptApi connectChatgptApi;

    @Autowired
    public MainController(ConnectChatgptApi connectChatgptApi) {
        this.connectChatgptApi = connectChatgptApi;
    }

    @GetMapping("/")
    public String hello(Model model) {
        model.addAttribute("chatgptObj", new ChatgptObj());
        return "main";
    }

    @PostMapping("/chatgpt")
    public String chatgpt(@ModelAttribute ChatgptObj chatgptObj) {
        connectChatgptApi.setTextFromChatgpi(chatgptObj);
        return "main";
    }
}