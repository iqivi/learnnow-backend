package com.learnnow.message.controller;

import com.learnnow.message.service.MessageVoteService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message/vote")
public class MessageVoteController {
    private MessageVoteService messageVoteService;

    public MessageVoteController(MessageVoteService messageVoteService) {
        this.messageVoteService = messageVoteService;
    }
}
