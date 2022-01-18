package org.springframework.samples.parchisYOca.playerGooseStats;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Set;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PlayerGooseStatsServiceTests {

    protected final String USERNAME = "ManuK";
    protected final String MATCH_CODE_1 = "09yupo";
    protected final String MATCH_CODE_2 = "JANORL";
    protected final String MATCH_CODE_3 = "123456";
    protected final String MATCH_CODE_4 = "9floep";

    @Autowired
    protected GooseMatchService gooseMatchService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected PlayerGooseStatsService playerGooseStatsService;

    @BeforeEach
    public void addMatchesWithDates(){
        //Populate stats
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        PlayerGooseStats pgs1 = new PlayerGooseStats();
        pgs1.setPlayer(player);
        pgs1.setHasWon(1);
        pgs1.setLandedGeese(20);
        pgs1.setLandedDice(30);
        pgs1.setInGameId(1);
        playerGooseStatsService.saveStats(pgs1);
        PlayerGooseStats pgs2 = new PlayerGooseStats();
        pgs2.setPlayer(player);
        pgs2.setHasWon(1);
        pgs2.setInGameId(3);
        playerGooseStatsService.saveStats(pgs2);
        PlayerGooseStats pgs3 = new PlayerGooseStats();
        pgs3.setPlayer(player);
        pgs3.setLandedDeath(2);
        pgs3.setLandedGeese(50);
        pgs3.setLandedDice(80);
        pgs3.setInGameId(1);
        playerGooseStatsService.saveStats(pgs3);

        //Populate with matches
        GooseMatch newMatch1 = new GooseMatch();
        String matchCode1 = MATCH_CODE_1;
        newMatch1.setMatchCode(matchCode1);
        newMatch1.setStartDate(new Date());
        newMatch1.setId(1);
        newMatch1.setEndDate(new Date());
        Set<PlayerGooseStats> statsOfGame1 = newMatch1.getStats();
        statsOfGame1.add(pgs1);
        statsOfGame1.add(pgs2);
        statsOfGame1.add(pgs3);
        GooseMatch addedMatch1 = gooseMatchService.save(newMatch1);
        GooseMatch newMatch2 = new GooseMatch();
        String matchCode2 = MATCH_CODE_2;
        newMatch2.setStartDate(new Date());
        newMatch2.setMatchCode(matchCode2);
        newMatch2.setId(2);
        newMatch2.setEndDate(new Date());
        GooseMatch addedMatch2 = gooseMatchService.save(newMatch2);
        GooseMatch newMatch3 = new GooseMatch();
        String matchCode3 = MATCH_CODE_3;
        newMatch3.setEndDate(new Date());
        newMatch3.setMatchCode(matchCode3);
        newMatch3.setStartDate(new Date());
        newMatch3.setId(3);
        GooseMatch addedMatch3 = gooseMatchService.save(newMatch3);
        GooseMatch newMatch4 = new GooseMatch();
        String matchCode4 = MATCH_CODE_4;
        newMatch4.setMatchCode(matchCode4);
        newMatch4.setStartDate(new Date());
        newMatch4.setId(4);
        newMatch4.setEndDate(new Date());
        GooseMatch addedMatch4 = gooseMatchService.save(newMatch4);
    }

    @Test
    @Transactional
    public void testFindGooseStatsByUsernameAndMatchId() {
        Assertions.assertThat(playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(USERNAME, 1).isPresent());
        Assertions.assertThat(playerGooseStatsService.findGooseStatsByUsernamedAndMatchId("", 1).isEmpty());
    }

    @Test
    @Transactional
    public void testFindGooseStatsByInGameIdAndMatchId() {
        Assertions.assertThat(playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(3,2).isPresent());
        Assertions.assertThat(playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(10,10).isEmpty());
    }

    @Test
    @Transactional
    public void testFindGooseStatsByUsername() {
        PlayerGooseStats pgs = playerGooseStatsService.findById(1).get();
        Assertions.assertThat(playerGooseStatsService.findPlayerGooseStatsByUsername(USERNAME).size()).isEqualTo(3);
        Assertions.assertThat(playerGooseStatsService.findPlayerGooseStatsByUsername(USERNAME).contains(pgs));
        Assertions.assertThat(playerGooseStatsService.findPlayerGooseStatsByUsername("").size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Disabled
    public void testRemoveGooseStatsFromGame() {
        Integer matchId = 1;
        Integer statsToRemoveId = 1;
        GooseMatch gm = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(gm.getStats().size()).isEqualTo(1);
        gooseMatchService.removeGooseStatsFromGame(playerGooseStatsService.findById(statsToRemoveId).get(),matchId);
        Assertions.assertThat(gm.getStats().size()).isEqualTo(0);
    }

}
