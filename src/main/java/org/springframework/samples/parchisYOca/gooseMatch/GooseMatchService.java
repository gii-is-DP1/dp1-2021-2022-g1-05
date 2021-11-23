package org.springframework.samples.parchisYOca.gooseMatch;

import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

public class GooseMatchService {

    private GooseMatchRepository gooseMatchRepository;
    private PlayerGooseStatsRepository playerGooseStatsRepository;

    private static final Integer MATCH_CODE_LENGTH = 5;

    @Autowired
    public GooseMatchService(GooseMatchRepository gooseMatchRepository, PlayerGooseStatsRepository playerGooseStatsRepository){
        this.gooseMatchRepository = gooseMatchRepository;
        this.playerGooseStatsRepository = playerGooseStatsRepository;
    }

    @Transactional
    public GooseMatch saveGooseMatchWithPlayer(Player player) throws DataAccessException {
        //Saves the match
        GooseMatch gooseMatch = new GooseMatch();
        Date date = new Date();
        gooseMatch.setStartDate(date);
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        gooseMatch.setMatchCode(matchCode);
        GooseMatch gooseMatchDB = gooseMatchRepository.save(gooseMatch);

        //Saves the relation between player and match
        PlayerGooseStats playerStats = new PlayerGooseStats();
        playerStats.setPlayer(player);
        playerStats.setGooseMatch(gooseMatchDB);
        playerGooseStatsRepository.save(playerStats);

        return gooseMatchDB;

    }
}
