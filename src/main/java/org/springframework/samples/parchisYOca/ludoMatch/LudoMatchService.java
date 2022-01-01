package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Transactional(readOnly = true)
    public Optional<LudoMatch> findludoMatchById(int id) throws DataAccessException {
        return ludoMatchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Iterable<LudoMatch> findAll() throws DataAccessException{
        return ludoMatchRepository.findAll();
    }

    @Transactional
    public Optional<LudoMatch> findLobbyByUsername(String username) throws DataAccessException{
        return ludoMatchRepository.findLobbyByUsername(username);
    }

    @Transactional
    public LudoMatch save(LudoMatch ludoMatch) throws DataAccessException {
        return ludoMatchRepository.save(ludoMatch);
    }

    @Transactional
    public LudoMatch saveludoMatchWithPlayer(LudoMatch ludoMatch, Player player, Boolean isOwner) throws DataAccessException {
        //Saves the match
        LudoMatch ludoMatchDB = ludoMatchRepository.save(ludoMatch);

        //Saves the relation between player and match
        PlayerLudoStats playerStats = new PlayerLudoStats();
        playerStats.setPlayer(player);
        playerStats.setLudoMatch(ludoMatchDB);

        //To assign the in game id
        if(ludoMatchDB.getStats()==null){
            playerStats.setInGameId(1);
        }else{
            Integer playersInGame = ludoMatchDB.getStats().size();
            playerStats.setInGameId(playersInGame+1);
        }

        if(isOwner){
            playerStats.setIsOwner(1);
            playerStats.setHasTurn(1);
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

    public boolean findEveryoneExceptOneLeft(LudoMatch ludoMatch) {
        Integer numberOfAfkPlayers = 0;
        Boolean res = false;

        for(PlayerLudoStats pgs : ludoMatch.getStats()){
            if(pgs.getPlayerLeft() == 1){
                numberOfAfkPlayers++;
            }
        }

        if(numberOfAfkPlayers == ludoMatch.getStats().size()-1){
            res = true;
            if(ludoMatch.getEndDate() == null){
                ludoMatch.setEndDate(new Date());
            }
        }

        return res;
    }
}
