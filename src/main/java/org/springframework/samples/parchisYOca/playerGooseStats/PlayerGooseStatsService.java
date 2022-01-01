package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlayerGooseStatsService {

    private PlayerGooseStatsRepository playerGooseStatsRepository;

    @Autowired
    public PlayerGooseStatsService(PlayerGooseStatsRepository playerGooseStatsRepository){
        this.playerGooseStatsRepository = playerGooseStatsRepository;
    }

    @Transactional(readOnly = true)
    public Optional<PlayerGooseStats> findGooseStatsByUsernamedAndMatchId(String username, Integer matchId) throws DataAccessException {
        return playerGooseStatsRepository.findPlayerGooseStatsByUsernamedAndMatchId(username, matchId);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerGooseStats> findPlayerGooseStatsByInGameIdAndMatchId(Integer inGameId, Integer matchId) throws DataAccessException {
        return playerGooseStatsRepository.findPlayerGooseStatsByInGameIdAndMatchId(inGameId, matchId);
    }
    @Transactional
    public PlayerGooseStats saveStats(PlayerGooseStats playerGooseStats) throws DataAccessException {
        return playerGooseStatsRepository.save(playerGooseStats);
    }

    @Transactional
    public void removeGooseStatsFromGame(Integer statsId, Integer gooseMatchId) throws DataAccessException {
        playerGooseStatsRepository.deletePlayerFromGame(statsId, gooseMatchId);
    }

    @Transactional
    public void removeAllGooseStatsFromGame(Integer gooseMatchId) throws DataAccessException {
        playerGooseStatsRepository.deleteStatsFromGame(gooseMatchId);
    }


}
