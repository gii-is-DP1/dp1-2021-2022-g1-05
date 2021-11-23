package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GooseMatchService {

    private GooseMatchRepository gooseMatchRepository;
    private PlayerGooseStatsRepository playerGooseStatsRepository;

    @Autowired
    public GooseMatchService(GooseMatchRepository gooseMatchRepository, PlayerGooseStatsRepository playerGooseStatsRepository){
        this.gooseMatchRepository = gooseMatchRepository;
        this.playerGooseStatsRepository = playerGooseStatsRepository;
    }

    @Transactional
    public GooseMatch saveGooseMatchWithPlayer(GooseMatch gooseMatch, Player player) throws DataAccessException {
        //Saves the match
        GooseMatch gooseMatchDB = gooseMatchRepository.save(gooseMatch);

        //Saves the relation between player and match
        PlayerGooseStats playerStats = new PlayerGooseStats();
        playerStats.setPlayer(player);
        playerStats.setGooseMatch(gooseMatchDB);
        playerGooseStatsRepository.save(playerStats);

        return gooseMatchDB;

    }
}
