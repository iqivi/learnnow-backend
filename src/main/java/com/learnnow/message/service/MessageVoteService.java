package com.learnnow.message.service;

import com.learnnow.message.repository.MessageVoteRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageVoteService {
    private final MessageVoteRepository messageVoteRepository;

    public MessageVoteService(MessageVoteRepository messageVoteRepository) {
        this.messageVoteRepository = messageVoteRepository;
    }
}
