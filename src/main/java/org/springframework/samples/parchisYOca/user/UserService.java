package org.springframework.samples.parchisYOca.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.user.UserRepository;
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
    public void save(User user) throws DataAccessException {
        userRepository.save(user);
    }

    @Transactional
    public void delete(User user) throws DataAccessException{
        userRepository.delete(user);
    }

}
