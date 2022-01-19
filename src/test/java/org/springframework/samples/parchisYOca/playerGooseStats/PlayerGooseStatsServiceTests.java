package org.springframework.samples.parchisYOca.playerGooseStats;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PlayerGooseStatsServiceTests {

    protected final String USERNAME = "ManuK";
    protected final String USERNAME_2 = "pedro";
    protected final String USERNAME_3 = "marioespiro";
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
        Player player2 = playerService.findPlayerByUsername(USERNAME_2).get();
        Player player3 = playerService.findPlayerByUsername(USERNAME_3).get();
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
        PlayerGooseStats pgs4 = new PlayerGooseStats();
        pgs4.setPlayer(player2);
        pgs4.setLandedDeath(10);
        pgs4.setInGameId(2);
        pgs4.setHasWon(1);
        playerGooseStatsService.saveStats(pgs4);
        PlayerGooseStats pgs5 = new PlayerGooseStats();
        pgs5.setPlayer(player3);
        pgs5.setLandedGeese(60);
        pgs5.setLandedDice(10);
        pgs5.setInGameId(4);
        playerGooseStatsService.saveStats(pgs5);

        //Populate with matches
        GooseMatch newMatch1 = new GooseMatch();
        String matchCode1 = MATCH_CODE_1;
        newMatch1.setMatchCode(matchCode1);
        newMatch1.setStartDate(new Date());
        newMatch1.setId(1);
        newMatch1.setEndDate(new Date());
        Set<PlayerGooseStats> statsOfGame1 = Set.of(pgs1, pgs2,pgs3);
        newMatch1.setStats(statsOfGame1);
        GooseMatch addedMatch1 = gooseMatchService.save(newMatch1);
        GooseMatch newMatch2 = new GooseMatch();
        String matchCode2 = MATCH_CODE_2;
        newMatch2.setStartDate(new Date());
        newMatch2.setMatchCode(matchCode2);
        newMatch2.setId(2);
        newMatch2.setEndDate(new Date());
        Set<PlayerGooseStats> statsOfGame2 = Set.of(pgs4);
        newMatch2.setStats(statsOfGame2);
        GooseMatch addedMatch2 = gooseMatchService.save(newMatch2);
        GooseMatch newMatch3 = new GooseMatch();
        String matchCode3 = MATCH_CODE_3;
        newMatch3.setEndDate(new Date());
        newMatch3.setMatchCode(matchCode3);
        newMatch3.setStartDate(new Date());
        Set<PlayerGooseStats> statsOfGame3 = Set.of(pgs5);
        newMatch3.setStats(statsOfGame3);
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
    public void testSumStats() {
        Collection<PlayerGooseStats> pgs = playerGooseStatsService.findPlayerGooseStatsByUsername(USERNAME);
        PlayerGooseStats pgsAfterSum = new PlayerGooseStats(); //Stats same as the sum of every stat of ManuK
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        pgsAfterSum.setPlayer(player);
        pgsAfterSum.setHasWon(2);
        pgsAfterSum.setLandedGeese(70);
        pgsAfterSum.setLandedDice(110);
        pgsAfterSum.setLandedDeath(2);

        Assertions.assertThat(playerGooseStatsService.sumStats(pgs).getPlayer()).isEqualTo(pgsAfterSum.getPlayer());
        Assertions.assertThat(playerGooseStatsService.sumStats(pgs).getHasWon()).isEqualTo(pgsAfterSum.getHasWon());
        Assertions.assertThat(playerGooseStatsService.sumStats(pgs).getLandedGeese()).isEqualTo(pgsAfterSum.getLandedGeese());
        Assertions.assertThat(playerGooseStatsService.sumStats(pgs).getLandedDice()).isEqualTo(pgsAfterSum.getLandedDice());
        Assertions.assertThat(playerGooseStatsService.sumStats(pgs).getLandedDeath()).isEqualTo(pgsAfterSum.getLandedDeath());
        assertThrows(NullPointerException.class, () ->{
            playerGooseStatsService.sumStats(null);
        });
    }

    @Test
    @Transactional
    public void testSumStatsByPlayer() {
        Collection<PlayerGooseStats> pgs1 = playerGooseStatsService.findPlayerGooseStatsByUsername(USERNAME_3);
        Collection<PlayerGooseStats> pgs2 = playerGooseStatsService.findPlayerGooseStatsByUsername(USERNAME_2);
        pgs1.addAll(pgs2);
        PlayerGooseStats pgsAfterSum1 = new PlayerGooseStats();
        PlayerGooseStats pgsAfterSum2 = new PlayerGooseStats();
        Player player1 = playerService.findPlayerByUsername(USERNAME_2).get();
        pgsAfterSum1.setPlayer(player1);
        pgsAfterSum1.setLandedDeath(10);
        pgsAfterSum1.setHasWon(1);
        Player player2 = playerService.findPlayerByUsername(USERNAME_3).get();
        pgsAfterSum2.setPlayer(player2);
        pgsAfterSum2.setLandedGeese(60);
        pgsAfterSum2.setLandedDice(10);

        Assertions.assertThat(playerGooseStatsService.sumStatsByPlayer(pgs1).keySet().contains(USERNAME_3));
        Assertions.assertThat(playerGooseStatsService.sumStatsByPlayer(pgs1).keySet().contains(USERNAME_2));

        Assertions.assertThat(playerGooseStatsService.sumStatsByPlayer(pgs1).get(USERNAME_2).getLandedDeath()).isEqualTo(pgsAfterSum1.getLandedDeath());
        Assertions.assertThat(playerGooseStatsService.sumStatsByPlayer(pgs1).get(USERNAME_2).getHasWon()).isEqualTo(pgsAfterSum1.getHasWon());

        Assertions.assertThat(playerGooseStatsService.sumStatsByPlayer(pgs1).get(USERNAME_3).getLandedDice()).isEqualTo(pgsAfterSum2.getLandedDice());
        Assertions.assertThat(playerGooseStatsService.sumStatsByPlayer(pgs1).get(USERNAME_3).getLandedGeese()).isEqualTo(pgsAfterSum2.getLandedGeese());

        assertThrows(NullPointerException.class, () ->{
            playerGooseStatsService.sumStats(null);
        });
    }

    @Test
    @Transactional
    public void testTop3ByGooseWins() {
        Set<PlayerGooseStats> allStats = new HashSet<>(playerGooseStatsService.findAll());

        Assertions.assertThat(playerGooseStatsService.top3MostGooseWins(allStats, "mostWins").get(0).getPlayer().getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(playerGooseStatsService.top3MostGooseWins(allStats, "mostWins").get(1).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_2);
        Assertions.assertThat(playerGooseStatsService.top3MostGooseWins(allStats, "mostWins").get(2).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_3);


        assertThrows(NullPointerException.class, () ->{
            playerGooseStatsService.sumStats(null);
        });
    }

    @Test
    @Transactional
    public void testTop3ByLandedGeese() {
        Set<PlayerGooseStats> allStats = new HashSet<>(playerGooseStatsService.findAll());

        Assertions.assertThat(playerGooseStatsService.top3MostGooseWins(allStats, "landedGeese").get(0).getPlayer().getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(playerGooseStatsService.top3MostGooseWins(allStats, "landedGeese").get(1).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_3);
        Assertions.assertThat(playerGooseStatsService.top3MostGooseWins(allStats, "landedGeese").get(2).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_2);


        assertThrows(NullPointerException.class, () ->{
            playerGooseStatsService.sumStats(null);
        });
    }

}
