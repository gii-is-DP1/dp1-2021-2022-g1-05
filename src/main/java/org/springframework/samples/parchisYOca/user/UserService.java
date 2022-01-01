package org.springframework.samples.parchisYOca.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public int userCount() throws DataAccessException {
        return (int) userRepository.count();
    }

    @Transactional(readOnly = true)
    public Iterable<User> findAll() throws DataAccessException{
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findUserByUsername(String username) throws DataAccessException{
        return userRepository.findById(username);
    }

    @Transactional
    public void saveUser(User user) throws DataAccessException {
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(User user) throws DataAccessException {
        userRepository.delete(user);
    }

    @Transactional
    public Boolean isAuthenticated() throws DataAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Boolean ac = false;

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
