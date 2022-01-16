package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GooseMatchService {

    private GooseMatchRepository gooseMatchRepository;

    @Autowired
    private PlayerGooseStatsService playerGooseStatsService;

    @Autowired
    public GooseMatchService(GooseMatchRepository gooseMatchRepository){
        this.gooseMatchRepository = gooseMatchRepository;

    }

    public Boolean findEveryoneExceptOneLeft(GooseMatch gooseMatch){
        Integer numberOfAfkPlayers = 0;
        Boolean res = false;

        for(PlayerGooseStats pgs : gooseMatch.getStats()){
            if(pgs.getPlayerLeft() == 1){
                numberOfAfkPlayers++;
            }
        }

        if(numberOfAfkPlayers == gooseMatch.getStats().size()-1){
            res = true;
            if(gooseMatch.getEndDate() == null){
                gooseMatch.setEndDate(new Date());
            }
        }

        return res;
    }

    @Transactional(readOnly = true)
    public Optional<GooseMatch> findGooseMatchByMatchCode(String matchCode) throws DataAccessException{
        return gooseMatchRepository.findMatchByMatchCode(matchCode);
    }

    @Transactional(readOnly = true)
    public Optional<GooseMatch> findGooseMatchById(int id) throws DataAccessException {
        return gooseMatchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Iterable<GooseMatch> findAll() throws DataAccessException{
        return gooseMatchRepository.findAll();
    }

    @Transactional(readOnly=true)
    public Optional<GooseMatch> findLobbyByUsername(String username) throws DataAccessException{
        return gooseMatchRepository.findLobbyByUsername(username);
    }

    @Transactional(readOnly=true)
    public Collection<GooseMatch> findEndedGooseMatches() throws DataAccessException{
        return gooseMatchRepository.findEndedGooseMatches();
    }

    @Transactional(readOnly=true)
    public Collection<GooseMatch> findMatchesByUsername(String username) throws DataAccessException{
        return gooseMatchRepository.findMatchesByUsername(username);
    }

    @Transactional(readOnly=true)
    public Collection<GooseMatch> findMatchesByStartDate(Date date) throws DataAccessException{
        return gooseMatchRepository.findGooseMatchByStartDate(date);
    }

    @Transactional(readOnly=true)
    public Collection<GooseMatch> findMatchesByEndDate(Date date) throws DataAccessException{
        return gooseMatchRepository.findGooseMatchByEndDate(date);
    }

    @Transactional
    public GooseMatch save(GooseMatch gooseMatch) throws DataAccessException {
        return gooseMatchRepository.save(gooseMatch);
    }

    @Transactional
    public GooseMatch saveGooseMatchWithPlayer(GooseMatch gooseMatch, Player player, Boolean isOwner) throws DataAccessException {
        //Saves the match
        GooseMatch gooseMatchDB = gooseMatchRepository.save(gooseMatch);

        //Saves the relation between player and match
        PlayerGooseStats playerStats = new PlayerGooseStats();
        playerStats.setPlayer(player);
        playerStats.setGooseMatch(gooseMatchDB);
        Set<PlayerGooseStats> statsSet = new HashSet<>();

        //To assign the in game id
        if (gooseMatchDB.getStats() != null) {
            Integer playersInGame = gooseMatchDB.getStats().size();
            playerStats.setInGameId(playersInGame);
            statsSet = gooseMatchDB.getStats();
        }
        if (isOwner) {
            playerStats.setIsOwner(1);
            playerStats.setHasTurn(1);

        }
        PlayerGooseStats addedStats = playerGooseStatsService.saveStats(playerStats);

        statsSet.add(addedStats);
        gooseMatchDB.setStats(statsSet);
        gooseMatchDB = gooseMatchRepository.save(gooseMatchDB);

        return gooseMatchDB;

    }
}
