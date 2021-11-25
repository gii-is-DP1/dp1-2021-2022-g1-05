package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public class GooseMatchService {

    private GooseMatchRepository gooseMatchRepository;
    private PlayerGooseStatsRepository playerGooseStatsRepository;

    @Autowired
    public GooseMatchService(GooseMatchRepository gooseMatchRepository, PlayerGooseStatsRepository playerGooseStatsRepository){
        this.gooseMatchRepository = gooseMatchRepository;
        this.playerGooseStatsRepository = playerGooseStatsRepository;
    }

    @Transactional(readOnly = true)
    public Optional<GooseMatch> findGooseMatchByMatchCode(String matchCode) throws DataAccessException{
        return gooseMatchRepository.findMatchByMatchCode(matchCode);
    }

    @Transactional(readOnly = true)
    public GooseMatch findGooseMatchById(int id) throws DataAccessException {
        return gooseMatchRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Iterable<GooseMatch> findAll() throws DataAccessException{
        return gooseMatchRepository.findAll();
    }

    @Transactional
    public Collection<GooseMatch> findLobbyByUsername(String username) throws DataAccessException{
        return gooseMatchRepository.findLobbyByUsername(username);
    }

    @Transactional
    public GooseMatch save(GooseMatch gooseMatch) throws DataAccessException {
        gooseMatchRepository.save(gooseMatch);
        return gooseMatch;
    }

    @Transactional
    public GooseMatch saveGooseMatchWithPlayer(GooseMatch gooseMatch, Player player) throws DataAccessException {
        //Saves the match
        GooseMatch gooseMatchDB = gooseMatchRepository.save(gooseMatch);

        //Saves the relation between player and match
        PlayerGooseStats playerStats = new PlayerGooseStats();
        playerStats.setPlayer(player);
        playerStats.setGooseMatch(gooseMatchDB);
        playerGooseStatsRepository.save(playerStats);

        return gooseMatchDB;

    }

    @Transactional
    public GooseMatch saveGooseMatchWithOwner(GooseMatch gooseMatch, Player player) throws DataAccessException {
        //Saves the match
        GooseMatch gooseMatchDB = gooseMatchRepository.save(gooseMatch);

        //Saves the relation between player and match
        PlayerGooseStats playerStats = new PlayerGooseStats();
        playerStats.setPlayer(player);
        playerStats.setGooseMatch(gooseMatchDB);
        playerStats.setIsOwner(1);
        playerGooseStatsRepository.save(playerStats);

        return gooseMatchDB;

    }
}
