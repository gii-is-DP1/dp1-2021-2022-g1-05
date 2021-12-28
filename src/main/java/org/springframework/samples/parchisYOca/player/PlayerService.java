package org.springframework.samples.parchisYOca.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Service
public class PlayerService {

    private PlayerRepository playerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthoritiesService authoritiesService;

    @Autowired
    private PlayerGooseStatsService playerGooseStatsService;

    @Autowired
    private PlayerLudoStatsService playerLudoStatsService;



    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayerById(int id) throws DataAccessException {
        return playerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayerByUsername(String username) throws DataAccessException {
        return playerRepository.findPlayerByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayerByEmail(String email) throws DataAccessException {
        return playerRepository.findPlayerByEmail(email);
    }


    @Transactional
    public Player savePlayer(Player player) throws DataAccessException{
        Player playerDB;
        //Check if player already exists (in other words, if this is and edit instead of a creation)
        //and if so, set the username so that it matches the current one, just in case
        if(playerRepository.findById(player.getId()).isPresent()) {
            User userIntroduced = player.getUser();
            userIntroduced.setUsername(playerRepository.findById(player.getId()).get().getUser().getUsername());
            player.setUser(userIntroduced);

            playerDB = playerRepository.save(player);
            userService.saveUser(userIntroduced);
        } else { //If player doens't exist, create it normally

            //creating player
            playerDB = playerRepository.save(player);
            //creating user
            userService.saveUser(player.getUser());
            //creating authorities
            authoritiesService.saveAuthorities(player.getUser().getUsername(), "player");
        }

        return playerDB;

    }

    @Transactional(readOnly = true)
    public Iterable<Player> findAll() throws DataAccessException {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Iterable<Player> findAllFilteringByUsername(String username) throws DataAccessException {
        return playerRepository.findAllPlayersFilterByUsername(username);
    }

    @Transactional
    public void disable(Player player) throws DataAccessException {
        player.getUser().setEnabled(false);

    }

    @Transactional
    public void enable(Player player) throws DataAccessException {
        player.getUser().setEnabled(true);
    }

    @Transactional
    public void delete(Player player) throws DataAccessException {

        playerRepository.delete(player);
        userService.deleteUser(player.getUser());
    }
}
