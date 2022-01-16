package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LudoMatchService {

    private LudoMatchRepository ludoMatchRepository;
    private PlayerLudoStatsService playerLudoStatsService;

    @Autowired
    public LudoMatchService(LudoMatchRepository ludoMatchRepository, PlayerLudoStatsService playerLudoStatsService){
        this.ludoMatchRepository = ludoMatchRepository;
        this.playerLudoStatsService = playerLudoStatsService;
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

    @Transactional(readOnly = true)
    public Optional<LudoMatch> findLobbyByUsername(String username) throws DataAccessException{
        return ludoMatchRepository.findLobbyByUsername(username);
    }

    @Transactional(readOnly = true)
    public Collection<LudoMatch> findEndedLudoMatches() throws DataAccessException{
        return ludoMatchRepository.findEndedLudoMatches();
    }

    @Transactional(readOnly = true)
    public Collection<LudoMatch> findMatchesByUsername(String username) throws DataAccessException{
        return ludoMatchRepository.findMatchesByUsername(username);
    }

    @Transactional(readOnly=true)
    public Collection<LudoMatch> findMatchesByStartDate(Date date) throws DataAccessException{
        return ludoMatchRepository.findLudoMatchByStartDate(date);
    }

    @Transactional(readOnly=true)
    public Collection<LudoMatch> findMatchesByEndDate(Date date) throws DataAccessException{
        return ludoMatchRepository.findLudoMatchByEndDate(date);
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
        Set<PlayerLudoStats> statsSet = new HashSet<>();

        //To assign the in game id
        if(ludoMatchDB.getStats() != null) {
            Integer playersInGame = ludoMatchDB.getStats().size();
            playerStats.setInGameId(playersInGame);
            statsSet = ludoMatchDB.getStats();
        }

        if(isOwner){
            playerStats.setIsOwner(1);
            playerStats.setHasTurn(1);
        }
        PlayerLudoStats addedStats = playerLudoStatsService.saveStats(playerStats);

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
