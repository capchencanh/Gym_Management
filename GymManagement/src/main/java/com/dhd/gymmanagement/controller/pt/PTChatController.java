package com.dhd.gymmanagement.controller.pt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pt")
public class PTChatController {

    @GetMapping("/chat")
    public String showChatPage() {
        return "pt/chat";
    }
}
