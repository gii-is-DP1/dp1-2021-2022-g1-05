package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@Slf4j
public class PlayerGooseStatsService {

    private PlayerGooseStatsRepository  playerGooseStatsRepository;

    @Autowired
    public PlayerGooseStatsService(PlayerGooseStatsRepository playerGooseStatsRepository){
        this.playerGooseStatsRepository = playerGooseStatsRepository;
    }

    //Used to show stats in profile
    public PlayerGooseStats sumStats(Collection<PlayerGooseStats> statsList){
    	log.debug("Calculating the sum of all Goose stats");
        PlayerGooseStats stats = new PlayerGooseStats();
        for(PlayerGooseStats pgs : statsList){
            stats.setDoubleRolls(pgs.getDoubleRolls() + stats.getDoubleRolls());
            stats.setLandedGeese(pgs.getLandedGeese() + stats.getLandedGeese());
            stats.setLandedBridges(pgs.getLandedBridges() + stats.getLandedBridges());
            stats.setLandedDeath(pgs.getLandedDeath() + stats.getLandedDeath());
            stats.setLandedDice(pgs.getLandedDice() + stats.getLandedDice());
            stats.setLandedMaze(pgs.getLandedMaze() + stats.getLandedMaze());
            stats.setLandedInn(pgs.getLandedInn() + stats.getLandedInn());
            stats.setLandedJails(pgs.getLandedJails() + stats.getLandedJails());
            stats.setHasWon(pgs.getHasWon() + stats.getHasWon());
            stats.setPlayer(pgs.getPlayer());
        }
        return stats;
    }

    //Used to show rankings
    public Map<String, PlayerGooseStats> sumStatsByPlayer(Collection<PlayerGooseStats> statsList){
    	log.debug("Calculating the sum of all Goose stats by player");
        Map<String, PlayerGooseStats> map = new HashMap<>();
        for(PlayerGooseStats pgs : statsList){
            String username = pgs.getPlayer().getUser().getUsername();
            if(map.containsKey(username)){
                PlayerGooseStats stats = map.get(username);
                List<PlayerGooseStats> statsToSum = List.of(stats,pgs);
                PlayerGooseStats statsToAdd = sumStats(statsToSum);
                statsToAdd.setPlayer(pgs.getPlayer());
                map.put(username, statsToAdd);
            }else{
                map.put(username, pgs);
            }
        }
        return map;
    }

    //Used to show rankings
    public List<PlayerGooseStats> top3MostGooseWins(Set<PlayerGooseStats> setGooseStats, String statToCheck){
    	log.debug("Getting the top 3 of Goose");
        Map<String, PlayerGooseStats> gooseStatsByPlayer = sumStatsByPlayer(setGooseStats);
        PlayerGooseStats most = new PlayerGooseStats();
        PlayerGooseStats secondMost = new PlayerGooseStats();
        PlayerGooseStats thirdMost = new PlayerGooseStats();

        for(PlayerGooseStats pgs : gooseStatsByPlayer.values()){
            if(statToCheck.equals("mostWins")){
                Integer wins = pgs.getHasWon();
                if(wins >= most.getHasWon()){
                    thirdMost = secondMost;
                    secondMost = most;
                    most = pgs;
                } else if(wins >= secondMost.getHasWon()){
                    thirdMost = secondMost;
                    secondMost = pgs;
                } else if (wins >= thirdMost.getHasWon()){
                    thirdMost = pgs;
                }
            }else{
                Integer gooses = pgs.getLandedGeese();
                if(gooses >= most.getLandedGeese()){
                    thirdMost = secondMost;
                    secondMost = most;
                    most = pgs;
                } else if(gooses >= secondMost.getLandedGeese()){
                    thirdMost = secondMost;
                    secondMost = pgs;
                } else if (gooses >= thirdMost.getLandedGeese()){
                    thirdMost = pgs;
                }
            }

        }
        return List.of(most, secondMost, thirdMost);
    }

    @Transactional(readOnly = true)
    public Collection<PlayerGooseStats> findAll() throws DataAccessException {
    	log.debug("Getting all PlayerGooseStats");
        return playerGooseStatsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PlayerGooseStats> findById(Integer id) throws DataAccessException {
        log.debug("Getting the stats with id {}", id);
        return playerGooseStatsRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerGooseStats> findGooseStatsByUsernamedAndMatchId(String username, Integer matchId) throws DataAccessException {
    	log.debug("Getting the stats of {} in the Goose match with id {}", username, matchId);
        return playerGooseStatsRepository.findPlayerGooseStatsByUsernamedAndMatchId(username, matchId);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerGooseStats> findPlayerGooseStatsByInGameIdAndMatchId(Integer inGameId, Integer matchId) throws DataAccessException {
    	log.debug("Finding PlayerGooseStats of player with inGameId '{}' in Goose match with id '{}'",inGameId,matchId);
        return playerGooseStatsRepository.findPlayerGooseStatsByInGameIdAndMatchId(inGameId, matchId);
    }

    @Transactional(readOnly = true)
    public Collection<PlayerGooseStats> findPlayerGooseStatsByUsername(String username) throws DataAccessException {
    	log.debug("Finding {}'s PlayerGooseStats", username);
        return playerGooseStatsRepository.findPlayerGooseStatsByUsername(username);
    }


    @Transactional
    public PlayerGooseStats saveStats(PlayerGooseStats playerGooseStats) throws DataAccessException {
    	log.debug("Saving {}'s PlayerGooseStats", playerGooseStats.getPlayer().getUser().getUsername());
        return playerGooseStatsRepository.save(playerGooseStats);
    }

}
