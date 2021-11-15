package org.springframework.samples.parchisYOca.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

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
    public Optional<User> findUserById(int userId) throws DataAccessException{
        return userRepository.findById(userId);
    }


    @Transactional
    public void delete(User user) throws DataAccessException{
        userRepository.delete(user);
    }


    @Transactional
    public void saveUser(User user) throws DataAccessException {
        user.setEnabled(true);
        userRepository.save(user);
    }

}
