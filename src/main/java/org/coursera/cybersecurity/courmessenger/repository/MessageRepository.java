package org.coursera.cybersecurity.courmessenger.repository;

import java.util.List;

import org.coursera.cybersecurity.courmessenger.domain.Message;
import org.coursera.cybersecurity.courmessenger.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverOrderByCreationDateDesc(User loggedUser);

    List<Message> findBySenderOrderByCreationDateDesc(User loggedUser);

}
