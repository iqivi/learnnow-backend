package com.learnnow.message.repository;

import com.learnnow.message.model.MessageVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageVoteRepository extends JpaRepository<MessageVote, Long> {
}
