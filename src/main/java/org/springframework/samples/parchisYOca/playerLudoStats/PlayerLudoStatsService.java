package org.springframework.samples.parchisYOca.playerLudoStats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class PlayerLudoStatsService {

    private PlayerLudoStatsRepository playerLudoStatsRepository;

    //Used to show stats in profile
    public PlayerLudoStats sumStats(Collection<PlayerLudoStats> statsList){
    	log.info("Calculating the sum of all Ludo stats");
        PlayerLudoStats stats = new PlayerLudoStats();
        for(PlayerLudoStats pls : statsList){
            stats.setDoubleRolls(pls.getDoubleRolls() + stats.getDoubleRolls());
            stats.setEatenTokens(pls.getEatenTokens() + stats.getEatenTokens());
            stats.setTakeOuts(pls.getTakeOuts() + stats.getTakeOuts());
            stats.setGreedyRolls(pls.getGreedyRolls() + stats.getGreedyRolls());
            stats.setScoredTokens(pls.getScoredTokens() + stats.getScoredTokens());
            stats.setWalkedSquares(pls.getWalkedSquares() + stats.getWalkedSquares());
            stats.setHasWon(pls.getHasWon() + stats.getHasWon());
            stats.setPlayer(pls.getPlayer());
        }
        return stats;
    }

    //Used to show rankings
    public Map<String, PlayerLudoStats> sumStatsByPlayer(Collection<PlayerLudoStats> statsList){
        Map<String, PlayerLudoStats> map = new HashMap<>();
        for(PlayerLudoStats pls : statsList){
            String username = pls.getPlayer().getUser().getUsername();
            if(!map.containsKey(username)){
                map.put(username, pls);
            }else{
                PlayerLudoStats stats = map.get(username);
                List<PlayerLudoStats> statsToSum = List.of(stats,pls);
                PlayerLudoStats statsToAdd = sumStats(statsToSum);
                stats.setPlayer(pls.getPlayer());
                map.put(username, statsToAdd);
            }
        }
        return map;
    }

    //Used to show rankings
    public List<PlayerLudoStats> top3MostLudoWins(Set<PlayerLudoStats> setLudoStats, String statToCheck){
        Map<String, PlayerLudoStats> ludoStatsByPlayer = sumStatsByPlayer(setLudoStats);
        PlayerLudoStats most = new PlayerLudoStats();
        PlayerLudoStats secondMost = new PlayerLudoStats();
        PlayerLudoStats thirdMost = new PlayerLudoStats();

        for(PlayerLudoStats pls : ludoStatsByPlayer.values()){
            if(statToCheck.equals("mostWins")){
                Integer wins = pls.getHasWon();
                if(wins >= most.getHasWon()){
                    thirdMost = secondMost;
                    secondMost = most;
                    most = pls;
                } else if(wins >= secondMost.getHasWon()){
                    thirdMost = secondMost;
                    secondMost = pls;
                } else if (wins >= thirdMost.getHasWon()){
                    thirdMost = pls;
                }
            }else{
                Integer eatenTokens = pls.getEatenTokens();
                if(eatenTokens >= most.getHasWon()){
                    thirdMost = secondMost;
                    secondMost = most;
                    most = pls;
                } else if(eatenTokens >= secondMost.getEatenTokens()){
                    thirdMost = secondMost;
                    secondMost = pls;
                } else if (eatenTokens >= thirdMost.getEatenTokens()){
                    thirdMost = pls;
                }
            }

        }
        return List.of(most, secondMost, thirdMost);
    }

    @Autowired
    public PlayerLudoStatsService(PlayerLudoStatsRepository playerLudoStatsRepository){
        this.playerLudoStatsRepository = playerLudoStatsRepository;
    }

    @Transactional(readOnly = true)
    public Iterable<PlayerLudoStats> findAll() throws DataAccessException {
        return playerLudoStatsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PlayerLudoStats> findById(Integer id) throws DataAccessException {
        return playerLudoStatsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerLudoStats> findPlayerLudoStatsByUsernameAndMatchId(String username, Integer matchId) throws DataAccessException {
        return playerLudoStatsRepository.findPlayerLudoStatsByUsernameAndMatchId(username, matchId);
    }

    @Transactional(readOnly = true)
    public Collection<PlayerLudoStats> findPlayerLudoStatsByUsername(String username) throws DataAccessException {
        return playerLudoStatsRepository.findPlayerLudoStatsByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerLudoStats> findPlayerLudoStatsByInGameIdAndMatchId(Integer inGameId,Integer matchId){
        return playerLudoStatsRepository.findPlayerLudoStatsByInGameIdAndMatchId(inGameId,matchId);
    }

    @Transactional
    public PlayerLudoStats saveStats(PlayerLudoStats playerLudoStats) throws DataAccessException {
        return playerLudoStatsRepository.save(playerLudoStats);
    }

}
