package org.springframework.samples.parchisYOca.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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

    @Transactional(readOnly = true)
    public Optional<Player> findWinnerByGooseMatchCode(String matchCode) throws DataAccessException {
        return playerRepository.findWinnerByGooseMatchCode(matchCode);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findWinnerByLudoMatchCode(String matchCode) throws DataAccessException {
        return playerRepository.findWinnerByLudoMatchCode(matchCode);
    }


    @Transactional
    public Player savePlayer(Player player) throws DataAccessException{
        Player playerDB;
        //Check if player already exists (in other words, if this is and edit instead of a creation)
        //and if so, set the username so that it matches the current one, just in case
        if(player.getId()!=null) {
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
    public Collection<Player> findAll() throws DataAccessException {
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Slice<Player> findAllPaging(Pageable pageable) throws DataAccessException {
        return playerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Player> findAllFilteringByUsername(String username, Pageable pageable) throws DataAccessException {
        return playerRepository.findAllPlayersFilterByUsername(username, pageable);
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
