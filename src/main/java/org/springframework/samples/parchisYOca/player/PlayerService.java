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

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;


@Service
@Slf4j
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
    	log.debug("Finding player with id: {}", id);
        return playerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayerByUsername(String username) throws DataAccessException {
    	log.debug("Finding player with username: {}", username);
        return playerRepository.findPlayerByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findPlayerByEmail(String email) throws DataAccessException {
    	log.debug("Finding user with email: {}", email);
        return playerRepository.findPlayerByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findWinnerByGooseMatchCode(String matchCode) throws DataAccessException {
    	log.debug("Finding winner of the Goose Match with code: {}", matchCode);
        return playerRepository.findWinnerByGooseMatchCode(matchCode);
    }

    @Transactional(readOnly = true)
    public Optional<Player> findWinnerByLudoMatchCode(String matchCode) throws DataAccessException {
    	log.debug("Finding winner of the Ludo Match with code: {}", matchCode);
        return playerRepository.findWinnerByLudoMatchCode(matchCode);
    }


    @Transactional
    public Player savePlayer(Player player) throws DataAccessException{
        Player playerDB;
        log.info("Saving or updating player: {}", player.getUser().getUsername());
        //Check if player already exists (in other words, if this is and edit instead of a creation)
        //and if so, set the username so that it matches the current one, just in case
        if( player.getId()!=null) {
        	log.debug("Player {} already exists so we are updating",player.getUser().getUsername());

            User userIntroduced = player.getUser();
            log.debug("Modifying user for player: {}", player.getUser().getUsername());
            userIntroduced.setUsername(playerRepository.findById(player.getId()).get().getUser().getUsername());
            player.setUser(userIntroduced);
            playerDB = playerRepository.save(player);
            userService.saveUser(userIntroduced);

        } else { //If player doens't exist, create it normally
        	log.debug("Player {} doesn't exist so we are creating", player.getUser().getUsername());
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
    	log.debug("Finding all players");
        return playerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Slice<Player> findAllPaging(Pageable pageable) throws DataAccessException {
    	log.debug("Finding all players with page size: {}", pageable);
        return playerRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Slice<Player> findAllFilteringByUsername(String username, Pageable pageable) throws DataAccessException {
    	log.debug("Filtering by string '{}' with page size: {}", username, pageable);
        return playerRepository.findAllPlayersFilterByUsername(username, pageable);
    }

    @Transactional
    public void disable(Player player) throws DataAccessException {
    	log.debug("Disabling player: {}", player.getUser().getUsername());
        player.getUser().setEnabled(false);

    }

    @Transactional
    public void enable(Player player) throws DataAccessException {
    	log.debug("Enabling player: {}", player.getUser().getUsername());
        player.getUser().setEnabled(true);
    }

    @Transactional
    public void delete(Player player) throws DataAccessException {
    	log.debug("Deleting player: {}", player.getUser().getUsername());
        playerRepository.delete(player);
        userService.deleteUser(player.getUser());
    }
}
