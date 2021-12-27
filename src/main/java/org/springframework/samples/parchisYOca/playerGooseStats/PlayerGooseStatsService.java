package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchRepository;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
    public void saveStats(PlayerGooseStats playerGooseStats) throws DataAccessException {
        playerGooseStatsRepository.save(playerGooseStats);

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
