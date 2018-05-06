package org.coursera.cybersecurity.courmessenger.repository;

import java.util.List;

import org.coursera.cybersecurity.courmessenger.domain.Message;
import org.coursera.cybersecurity.courmessenger.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByReceiverOrderByCreationDateDesc(User loggedUser);

    List<Message> findBySenderOrderByCreationDateDesc(User loggedUser);

    @Query("select m from Message m where m.id = ?1 and (m.sender = ?2 or m.receiver = ?2)")
    Message findByIdAndUser(Long id, User loggedUser);

}
