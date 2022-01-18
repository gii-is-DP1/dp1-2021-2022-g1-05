package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
public class GooseMatchService {

    private GooseMatchRepository gooseMatchRepository;

    @Autowired
    private PlayerGooseStatsService playerGooseStatsService;

    @Autowired
    public GooseMatchService(GooseMatchRepository gooseMatchRepository){
        this.gooseMatchRepository = gooseMatchRepository;

    }

    public Boolean findEveryoneExceptOneLeft(GooseMatch gooseMatch){
    	log.debug("Finding everyone in a Goose match except the one that left");
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
    	log.debug("Finding Goose match with code '{}'", matchCode);
        return gooseMatchRepository.findMatchByMatchCode(matchCode);
    }

    @Transactional(readOnly = true)
    public Optional<GooseMatch> findGooseMatchById(int id) throws DataAccessException {
    	log.debug("Finding Goose match with id '{}'", id);
        return gooseMatchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Collection<GooseMatch> findAll() throws DataAccessException{
    	log.debug("Finding all Goose matches");
        return gooseMatchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Slice<GooseMatch> findAllPaging(Pageable pageable) throws DataAccessException {
    	log.debug("Finding all Goose matches with page size {}", pageable.getPageSize());
        return gooseMatchRepository.findAll(pageable);
    }

    @Transactional(readOnly=true)
    public Optional<GooseMatch> findLobbyByUsername(String username) throws DataAccessException{
    	log.debug("Finding lobby with player '{}'", username);
        return gooseMatchRepository.findLobbyByUsername(username);
    }

    @Transactional(readOnly=true)
    public Collection<GooseMatch> findEndedGooseMatches() throws DataAccessException{
    	log.debug("Finding all finished Goose matches");
        return gooseMatchRepository.findEndedGooseMatches();
    }

    @Transactional(readOnly=true)
    public Collection<GooseMatch> findMatchesByUsername(String username) throws DataAccessException{
    	log.debug("Finding all {}'s Goose matches", username);
        return gooseMatchRepository.findMatchesByUsername(username);
    }

    @Transactional(readOnly=true)
    public Slice<GooseMatch> findMatchesByUsernameWithPaging(String username, Pageable pageable) throws DataAccessException{
    	log.debug("Finding all {}'s Goose matches with page size {} and page number {}", username,pageable.getPageSize(), pageable.getPageNumber());
        return gooseMatchRepository.findMatchesByUsernameWithPaging(username, pageable);
    }

    @Transactional(readOnly=true)
    public Page<GooseMatch> findMatchesByStartDate(Date date, Pageable pageable) throws DataAccessException{
    	log.debug("Finding all Goose matches that started on '{}' with page size {} and page number {}", date.toString(), pageable.getPageSize(), pageable.getPageNumber());
        return gooseMatchRepository.findGooseMatchByStartDate(date, pageable);
    }

    @Transactional(readOnly=true)
    public Page<GooseMatch> findMatchesByEndDate(Date date, Pageable pageable) throws DataAccessException{
    	log.debug("Finding all Goose matches that ended on '{}' with page size {} and page number {}", date.toString(), pageable.getPageSize(), pageable.getPageNumber());
        return gooseMatchRepository.findGooseMatchByEndDate(date, pageable);
    }

    @Transactional
    public GooseMatch save(GooseMatch gooseMatch) throws DataAccessException {
    	log.debug("Saving Goose match with match code '{}'",gooseMatch.getMatchCode() );
        return gooseMatchRepository.save(gooseMatch);
    }

    @Transactional
    public Optional<GooseMatch> findMatchByPlayerGooseStats(PlayerGooseStats pgs) throws DataAccessException {
        log.debug("Finding Goose match with stats '{}'",pgs.toString() );
        return gooseMatchRepository.findMatchByPlayerGooseStats(pgs);
    }

    @Transactional
    public GooseMatch saveGooseMatchWithPlayer(GooseMatch gooseMatch, Player player, Boolean isOwner) throws DataAccessException {
    	log.debug("Saving Goose match with code '{}' and player '{}'", gooseMatch.getMatchCode(), player.getUser().getUsername());
        //Saves the match
        GooseMatch gooseMatchDB = gooseMatchRepository.save(gooseMatch);

        //Saves the relation between player and match
        PlayerGooseStats playerStats = new PlayerGooseStats();
        playerStats.setPlayer(player);
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

    @Transactional
    public void removeGooseStatsFromGame(PlayerGooseStats pgs, Integer gooseMatchId) throws DataAccessException {
        log.debug("Deleting PlayerGooseStats '{}' from matchId '{}'",pgs.toString(),gooseMatchId);
        GooseMatch gm = gooseMatchRepository.findById(gooseMatchId).get();
        Set<PlayerGooseStats> statsOfGame = gm.getStats();
        statsOfGame.remove(pgs);
        gm.setStats(statsOfGame);
        gooseMatchRepository.save(gm);
    }

    @Transactional
    public void removeAllGooseStatsFromGame(Integer gooseMatchId) throws DataAccessException {
        log.debug("Removing all PlayerGooseStats from match with id '{}'", gooseMatchId);
        GooseMatch gm = gooseMatchRepository.findById(gooseMatchId).get();
        Set<PlayerGooseStats> emptyStats = new HashSet<>();
        gm.setStats(emptyStats);
        gooseMatchRepository.save(gm);
    }
}
