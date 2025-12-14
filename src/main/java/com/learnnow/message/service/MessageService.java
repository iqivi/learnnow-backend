package com.learnnow.message.service;

import com.learnnow.message.repository.MessageRepository;
import com.learnnow.message.repository.MessageVoteRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
