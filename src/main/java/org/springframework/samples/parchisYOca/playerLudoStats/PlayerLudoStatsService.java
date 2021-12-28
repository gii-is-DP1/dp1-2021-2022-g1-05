package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PlayerLudoStatsService {

    private PlayerLudoStatsRepository playerLudoStatsRepository;

    @Autowired
    public PlayerLudoStatsService(PlayerLudoStatsRepository playerLudoStatsRepository){
        this.playerLudoStatsRepository = playerLudoStatsRepository;
    }

    @Transactional(readOnly = true)
    public PlayerLudoStats findPlayerLudoStatsByUsernameAndMatchId(String username, Integer matchId) throws DataAccessException {
        return playerLudoStatsRepository.findPlayerLudoStatsByUsernameAndMatchId(username, matchId).get();
    }

    @Transactional
    public void saveStats(PlayerLudoStats playerLudoStats) throws DataAccessException {
        playerLudoStatsRepository.save(playerLudoStats);

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
