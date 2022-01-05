package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class PlayerLudoStatsService {

    private PlayerLudoStatsRepository playerLudoStatsRepository;

    //Used to show stats in profile
    public PlayerLudoStats sumStats(Collection<PlayerLudoStats> statsList){
        PlayerLudoStats stats = new PlayerLudoStats();
        for(PlayerLudoStats pls : statsList){
            stats.setDoubleRolls(pls.getDoubleRolls() + stats.getDoubleRolls());
            stats.setEatenTokens(pls.getEatenTokens() + stats.getEatenTokens());
            stats.setCreatedBlocks(pls.getCreatedBlocks() + stats.getCreatedBlocks());
            stats.setTakeOuts(pls.getTakeOuts() + stats.getTakeOuts());
            stats.setGreedyRolls(pls.getGreedyRolls() + stats.getGreedyRolls());
            stats.setScoredTokens(pls.getScoredTokens() + stats.getScoredTokens());
            stats.setWalkedSquares(pls.getWalkedSquares() + stats.getWalkedSquares());
            stats.setHasWon(pls.getHasWon() + stats.getHasWon());
        }
        return stats;
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
    public Optional<PlayerLudoStats> findPlayerLudoStatsByUsernameAndMatchId(String username, Integer matchId) throws DataAccessException {
        return playerLudoStatsRepository.findPlayerLudoStatsByUsernameAndMatchId(username, matchId);
    }

    @Transactional(readOnly = true)
    public Collection<PlayerLudoStats> findPlayerLudoStatsByUsername(String username) throws DataAccessException {
        return playerLudoStatsRepository.findPlayerLudoStatsByUsername(username);
    }

    @Transactional
    public PlayerLudoStats saveStats(PlayerLudoStats playerLudoStats) throws DataAccessException {
        return playerLudoStatsRepository.save(playerLudoStats);
    }

    @Transactional
    public void removeLudoStatsFromGame(Integer statsId, Integer ludoMatchId) throws DataAccessException {
        playerLudoStatsRepository.deletePlayerFromGame(statsId, ludoMatchId);
    }

    @Transactional
    public void removeAllLudoStatsFromGame(Integer ludoMatchId) throws DataAccessException {
        playerLudoStatsRepository.deleteStatsFromGame(ludoMatchId);
    }
}
