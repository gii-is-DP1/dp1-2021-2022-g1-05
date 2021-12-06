package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchRepository;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;
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
    public PlayerGooseStats findGooseStatsByUsernamedAndMatchId(String username, Integer matchId) throws DataAccessException {
        return playerGooseStatsRepository.findPlayerGooseStatsByUsernamedAndMatchId(username, matchId).get();
    }

}
