package org.springframework.samples.parchisYOca.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class PlayerService {

    private PlayerRepository playerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public Player findPlayerById(int id) throws DataAccessException {
        return playerRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Player findPlayerByUsername(String username) throws DataAccessException {
        return playerRepository.findPlayerByUsername(username).get();
    }


    @Transactional
    public void savePlayer(Player player) throws DataAccessException {
        //creating player
        playerRepository.save(player);
        //creating user
        userService.saveUser(player.getUser());
        //creating authorities
        authoritiesService.saveAuthorities(player.getUser().getUsername(), "player");
    }

    @Transactional(readOnly = true)
    public Iterable<Player> findAll() throws DataAccessException {
        return playerRepository.findAll();
    }
}
