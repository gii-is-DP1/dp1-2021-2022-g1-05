package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
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
    	log.debug("Finding Ludo match with code '{}'",matchCode );
        return ludoMatchRepository.findMatchByMatchCode(matchCode);
    }

    @Transactional(readOnly = true)
    public Optional<LudoMatch> findludoMatchById(int id) throws DataAccessException {
    	log.debug("Finding Ludo match with id '{}'",id );
        return ludoMatchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Collection<LudoMatch> findAll() throws DataAccessException{
    	log.debug("Finding all Ludo matches");
        return ludoMatchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<LudoMatch> findAllPaging(Pageable pageable) throws DataAccessException{
    	log.debug("Finding all Ludo matches with page size '{}'", pageable);
        return ludoMatchRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<LudoMatch> findLobbyByUsername(String username) throws DataAccessException{
    	log.debug("Finding lobby with player '{}'", username);
        return ludoMatchRepository.findLobbyByUsername(username);
    }

    @Transactional(readOnly = true)
    public Collection<LudoMatch> findEndedLudoMatches() throws DataAccessException{
    	log.debug("Finding all finished Ludo matches");
        return ludoMatchRepository.findEndedLudoMatches();
    }

    @Transactional(readOnly = true)
    public Collection<LudoMatch> findMatchesByUsername(String username) throws DataAccessException{
    	log.debug("Finding all {}'s Ludo matches", username);
        return ludoMatchRepository.findMatchesByUsername(username);
    }

    @Transactional(readOnly = true)
    public Slice<LudoMatch> findMatchesByUsernameWithPaging(String username, Pageable pageable) throws DataAccessException{
    	log.debug("Finding all {}'s Ludo matches with page size '{}'", username,pageable);
        return ludoMatchRepository.findMatchesByUsernameWithPaging(username, pageable);
    }

    @Transactional(readOnly=true)
    public Page<LudoMatch> findMatchesByStartDate(Date date, Pageable pageable) throws DataAccessException{
    	log.debug("Finding all Ludo matches that started on '{}' with page size '{}'", date.toString(),pageable);
        return ludoMatchRepository.findLudoMatchByStartDate(date, pageable);
    }

    @Transactional(readOnly=true)
    public Page<LudoMatch> findMatchesByEndDate(Date date, Pageable pageable) throws DataAccessException{
    	log.debug("Finding all Ludo matches that ended on '{}' with page size '{}'", date.toString(),pageable);
        return ludoMatchRepository.findLudoMatchByEndDate(date, pageable);
    }

    @Transactional
    public LudoMatch save(LudoMatch ludoMatch) throws DataAccessException {
    	log.debug("Saving Ludo Match with match code '{}'", ludoMatch.getMatchCode());
        return ludoMatchRepository.save(ludoMatch);
    }

    @Transactional
    public Optional<LudoMatch> findMatchByPlayerLudoStats(PlayerLudoStats pls) throws DataAccessException {
        log.debug("Finding Ludo Match with stats '{}'", pls.toString());
        return ludoMatchRepository.findMatchByPlayerLudoStats(pls);
    }

    @Transactional
    public LudoMatch saveludoMatchWithPlayer(LudoMatch ludoMatch, Player player, Boolean isOwner) throws DataAccessException {
    	log.debug("Saving Ludo match with code '{}' and player '{}'", ludoMatch.getMatchCode(), player.getUser().getUsername());
        //Saves the match
        LudoMatch ludoMatchDB = ludoMatchRepository.save(ludoMatch);

        //Saves the relation between player and match
        PlayerLudoStats playerStats = new PlayerLudoStats();
        playerStats.setPlayer(player);
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
    	log.debug("Finding everyone in a Ludo match except the one that left");
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
            	log.debug("There's no one left so the match has ended");
                ludoMatch.setEndDate(new Date());
            }
        }

        return res;
    }

    @Transactional
    public void removeLudoStatsFromGame(PlayerLudoStats pls, Integer ludoMatchId) throws DataAccessException {
        log.debug("Deleting PlayerLudoStats '{}' from matchId '{}'",pls.toString(),ludoMatchId);
        LudoMatch lm = ludoMatchRepository.findById(ludoMatchId).get();
        Set<PlayerLudoStats> statsOfGame = lm.getStats();
        statsOfGame.remove(pls);
        lm.setStats(statsOfGame);
        ludoMatchRepository.save(lm);
    }

    @Transactional
    public void removeAllLudoStatsFromGame(Integer ludoMatchId) throws DataAccessException {
        log.debug("Removing all PlayerLudoStats from match with id '{}'", ludoMatchId);
        LudoMatch lm = ludoMatchRepository.findById(ludoMatchId).get();
        Set<PlayerLudoStats> emptyStats = new HashSet<>();
        lm.setStats(emptyStats);
        ludoMatchRepository.save(lm);
    }
}
