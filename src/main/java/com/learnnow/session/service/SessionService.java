package com.learnnow.session.service;

import com.learnnow.session.repository.SessionRepository;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    private SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }
}
