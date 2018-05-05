package org.coursera.cybersecurity.courmessenger.repository;

import org.coursera.cybersecurity.courmessenger.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    
    boolean existsByEmail(String email);
}
