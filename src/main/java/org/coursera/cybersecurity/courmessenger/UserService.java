package org.coursera.cybersecurity.courmessenger;

import org.coursera.cybersecurity.courmessenger.domain.User;
import org.coursera.cybersecurity.courmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder() //
                .password(user.getEncodedPassword()) //
                .username(user.getEmail()) //
                .authorities("ROLE_USER") //
                .build();
    }

}
