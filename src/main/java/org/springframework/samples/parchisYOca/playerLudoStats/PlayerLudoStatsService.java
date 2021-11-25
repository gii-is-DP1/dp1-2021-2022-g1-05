package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}
