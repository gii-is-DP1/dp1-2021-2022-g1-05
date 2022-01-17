package org.springframework.samples.parchisYOca.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Iterable<User> findAll() throws DataAccessException{
    	log.debug("Finding all users");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) throws DataAccessException{
    	log.debug("Finding user: {}", username);
        return userRepository.findById(username);
    }

    @Transactional
    public void saveUser(User user) throws DataAccessException {
    	log.debug("Saving user: {}", user.getUsername());
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) throws DataAccessException {
    	log.debug("Deleting user: {}", user.getUsername());
        userRepository.delete(user);
    }

    public Boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean ac = false;
        log.debug("Authenticating user: {}", authentication.getName());
        if(authentication.getPrincipal().toString() != "anonymousUser"){
            org.springframework.security.core.userdetails.User user =  (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            if (authentication.isAuthenticated() && findUserByUsername(user.getUsername()).isPresent()){
                ac = true;
            } else{
                authentication.setAuthenticated(false);
            }
        }
        return ac;
    }

}
