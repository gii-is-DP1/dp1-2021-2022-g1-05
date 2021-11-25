package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class LudoMatchService {

    private LudoMatchRepository ludoMatchRepository;
    private PlayerLudoStatsRepository playerLudoStatsRepository;

    @Autowired
    public LudoMatchService(LudoMatchRepository ludoMatchRepository, PlayerLudoStatsRepository playerLudoStatsRepository){
        this.ludoMatchRepository = ludoMatchRepository;
        this.playerLudoStatsRepository = playerLudoStatsRepository;
    }

    @Transactional(readOnly = true)
    public Optional<LudoMatch> findludoMatchByMatchCode(String matchCode) throws DataAccessException{
        return ludoMatchRepository.findMatchByMatchCode(matchCode);
    }

    @Transactional(readOnly = true) //TODO manu arregla esto, que devuelva optional pa tenerlo todo igual
    public LudoMatch findludoMatchById(int id) throws DataAccessException {
        return ludoMatchRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Iterable<LudoMatch> findAll() throws DataAccessException{
        return ludoMatchRepository.findAll();
    }

    @Transactional
    public Collection<LudoMatch> findLobbyByUsername(String username) throws DataAccessException{
        return ludoMatchRepository.findLobbyByUsername(username);
    }

    @Transactional
    public LudoMatch save(LudoMatch ludoMatch) throws DataAccessException {
        ludoMatchRepository.save(ludoMatch);
        return ludoMatch;
    }

    @Transactional
    public LudoMatch saveludoMatchWithPlayer(LudoMatch ludoMatch, Player player, Boolean isOwner) throws DataAccessException {
        //Saves the match
        LudoMatch ludoMatchDB = ludoMatchRepository.save(ludoMatch);

        //Saves the relation between player and match
        PlayerLudoStats playerStats = new PlayerLudoStats();
        playerStats.setPlayer(player);
        playerStats.setLudoMatch(ludoMatchDB);
        if(isOwner){
            playerStats.setIsOwner(1);
        }
        PlayerLudoStats addedStats = playerLudoStatsRepository.save(playerStats);

        //From here its the new method
        Set<PlayerLudoStats> statsSet = new HashSet<>();
        if (ludoMatchDB.getStats() != null) {
            statsSet = ludoMatchDB.getStats();
        }
        statsSet.add(addedStats);
        ludoMatchDB.setStats(statsSet);

        ludoMatchDB = ludoMatchRepository.save(ludoMatchDB);
        return ludoMatchDB;

    }
}
