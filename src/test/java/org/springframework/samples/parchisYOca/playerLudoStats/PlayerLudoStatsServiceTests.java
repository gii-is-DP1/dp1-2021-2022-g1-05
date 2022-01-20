package org.springframework.samples.parchisYOca.playerLudoStats;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PlayerLudoStatsServiceTests {

    protected final String USERNAME = "ManuK";
    protected final String USERNAME_2 = "pedro";
    protected final String USERNAME_3 = "marioespiro";
    protected final String MATCH_CODE_1 = "ran07o";
    protected final String MATCH_CODE_2 = "JNPORL";
    protected final String MATCH_CODE_3 = "1FG4TH";
    protected final String MATCH_CODE_4 = "IDKWHO";
    protected final Integer MATCH_ID = 1;
    protected final Integer EMPTY_MATCH_ID = 30;


    @Autowired
    protected LudoMatchService ludoMatchService;

    @Autowired
    protected PlayerService playerService;

    @Autowired
    protected  PlayerLudoStatsService playerLudoStatsService;

    @BeforeEach
    public void addMatchesWithDated(){
        Player player=playerService.findPlayerByUsername(USERNAME).get();
        Player player2=playerService.findPlayerByUsername(USERNAME_2).get();
        Player player3=playerService.findPlayerByUsername(USERNAME_3).get();
        PlayerLudoStats pgs1 = new PlayerLudoStats();
        pgs1.setPlayer(player);
        pgs1.setHasWon(1);
        pgs1.setInGameId(1);
        pgs1.setGreedyRolls(70);
        pgs1.setWalkedSquares(110);
        playerLudoStatsService.saveStats(pgs1);
        PlayerLudoStats pgs2 = new PlayerLudoStats();
        pgs2.setPlayer(player);
        pgs2.setHasWon(1);
        pgs2.setInGameId(3);
        pgs2.setEatenTokens(20);
        pgs2.setDoubleRolls(30);
        playerLudoStatsService.saveStats(pgs2);
        PlayerLudoStats pgs3 = new PlayerLudoStats();
        pgs3.setPlayer(player);
        pgs3.setInGameId(1);
        pgs3.setScoredTokens(40);
        pgs3.setTakeOuts(50);
        playerLudoStatsService.saveStats(pgs3);

        PlayerLudoStats pgs4 = new PlayerLudoStats();
        pgs4.setPlayer(player2);
        pgs4.setInGameId(2);
        pgs4.setHasWon(1);
        pgs4.setTakeOuts(10);
        playerLudoStatsService.saveStats(pgs4);
        PlayerLudoStats pgs5 = new PlayerLudoStats();
        pgs5.setPlayer(player3);
        pgs5.setInGameId(4);
        pgs5.setDoubleRolls(90);
        pgs5.setGreedyRolls(30);
        pgs5.setEatenTokens(5);
        playerLudoStatsService.saveStats(pgs5);

        //Populate with matches
        LudoMatch newMatch1 = new LudoMatch();
        String matchCode1 = MATCH_CODE_1;
        newMatch1.setMatchCode(matchCode1);
        newMatch1.setStartDate(new Date());
        newMatch1.setId(1);
        newMatch1.setEndDate(new Date());
        Set<PlayerLudoStats> statsOfGame1 = Set.of(pgs1, pgs2,pgs3);
        newMatch1.setStats(statsOfGame1);
        LudoMatch addedMatch1 = ludoMatchService.save(newMatch1);
        LudoMatch newMatch2 = new LudoMatch();
        String matchCode2 = MATCH_CODE_2;
        newMatch2.setStartDate(new Date());
        newMatch2.setMatchCode(matchCode2);
        newMatch2.setId(2);
        newMatch2.setEndDate(new Date());
        Set<PlayerLudoStats> statsOfGame2 = Set.of(pgs4);
        newMatch2.setStats(statsOfGame2);
        LudoMatch addedMatch2 = ludoMatchService.save(newMatch2);
        LudoMatch newMatch3 = new LudoMatch();
        String matchCode3 = MATCH_CODE_3;
        newMatch3.setEndDate(new Date());
        newMatch3.setMatchCode(matchCode3);
        newMatch3.setStartDate(new Date());
        Set<PlayerLudoStats> statsOfGame3 = Set.of(pgs5);
        newMatch3.setStats(statsOfGame3);
        newMatch3.setId(3);
        LudoMatch addedMatch3 = ludoMatchService.save(newMatch3);
        LudoMatch newMatch4 = new LudoMatch();
        String matchCode4 = MATCH_CODE_4;
        newMatch4.setMatchCode(matchCode4);
        newMatch4.setStartDate(new Date());
        newMatch4.setId(4);
        newMatch4.setEndDate(new Date());
        LudoMatch addedMatch4 = ludoMatchService.save(newMatch4);
    }

    @Test
    @Transactional
    public void testFindPlayerLudoStatsByUsernameAndMatchId(){
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(USERNAME,2).isPresent());
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId("", 1).isEmpty());
    }

    @Test
    @Transactional
    public void testFindPlayerLudoStatsByUsername(){
        PlayerLudoStats pls = playerLudoStatsService.findById(3).get();
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByUsername(USERNAME_2).size()).isEqualTo(1);
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByUsername(USERNAME_2).contains(pls));
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByUsername("").size()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void testFindPlayerLudoStatsByInGameIdAndMatchId(){
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(3,2).isPresent());
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(10,10).isEmpty());
    }

    @Test
    @Transactional
    public void testFindLudoStatsByMatchId() {
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByGame(MATCH_ID).size()).isEqualTo(3);
        Assertions.assertThat(playerLudoStatsService.findPlayerLudoStatsByGame(EMPTY_MATCH_ID).size()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void testSumStats(){
        Collection<PlayerLudoStats> pgs = playerLudoStatsService.findPlayerLudoStatsByUsername(USERNAME);
        PlayerLudoStats pgsAfterSum = new PlayerLudoStats(); //Stats same as the sum of every stat of ManuK
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        pgsAfterSum.setPlayer(player);
        pgsAfterSum.setHasWon(2);
        pgsAfterSum.setGreedyRolls(70);
        pgsAfterSum.setWalkedSquares(110);
        pgsAfterSum.setEatenTokens(20);
        pgsAfterSum.setDoubleRolls(30);
        pgsAfterSum.setScoredTokens(40);
        pgsAfterSum.setTakeOuts(50);

        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getPlayer()).isEqualTo(pgsAfterSum.getPlayer());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getHasWon()).isEqualTo(pgsAfterSum.getHasWon());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getGreedyRolls()).isEqualTo(pgsAfterSum.getGreedyRolls());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getWalkedSquares()).isEqualTo(pgsAfterSum.getWalkedSquares());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getEatenTokens()).isEqualTo(pgsAfterSum.getEatenTokens());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getDoubleRolls()).isEqualTo(pgsAfterSum.getDoubleRolls());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getScoredTokens()).isEqualTo(pgsAfterSum.getScoredTokens());
        Assertions.assertThat(playerLudoStatsService.sumStats(pgs).getTakeOuts()).isEqualTo(pgsAfterSum.getTakeOuts());

        assertThrows(NullPointerException.class, () ->{
            playerLudoStatsService.sumStats(null);
        });
    }

    @Test
    @Transactional
    public void testSumStatsByPlayer(){
        Collection<PlayerLudoStats> pgs1 = playerLudoStatsService.findPlayerLudoStatsByUsername(USERNAME_3);
        Collection<PlayerLudoStats> pgs2 = playerLudoStatsService.findPlayerLudoStatsByUsername(USERNAME_2);
        pgs1.addAll(pgs2);
        PlayerLudoStats pgsAfterSum1 = new PlayerLudoStats();
        PlayerLudoStats pgsAfterSum2 = new PlayerLudoStats();
        Player player1 = playerService.findPlayerByUsername(USERNAME_2).get();
        pgsAfterSum1.setPlayer(player1);
        pgsAfterSum1.setTakeOuts(10);
        pgsAfterSum1.setHasWon(1);
        Player player2 = playerService.findPlayerByUsername(USERNAME_3).get();
        pgsAfterSum2.setPlayer(player2);
        pgsAfterSum2.setGreedyRolls(30);
        pgsAfterSum2.setDoubleRolls(90);

        Assertions.assertThat(playerLudoStatsService.sumStatsByPlayer(pgs1).keySet().contains(USERNAME_3));
        Assertions.assertThat(playerLudoStatsService.sumStatsByPlayer(pgs1).keySet().contains(USERNAME_2));

        Assertions.assertThat(playerLudoStatsService.sumStatsByPlayer(pgs1).get(USERNAME_2).getTakeOuts()).isEqualTo(pgsAfterSum1.getTakeOuts());
        Assertions.assertThat(playerLudoStatsService.sumStatsByPlayer(pgs1).get(USERNAME_2).getHasWon()).isEqualTo(pgsAfterSum1.getHasWon());

        Assertions.assertThat(playerLudoStatsService.sumStatsByPlayer(pgs1).get(USERNAME_3).getDoubleRolls()).isEqualTo(pgsAfterSum2.getDoubleRolls());
        Assertions.assertThat(playerLudoStatsService.sumStatsByPlayer(pgs1).get(USERNAME_3).getGreedyRolls()).isEqualTo(pgsAfterSum2.getGreedyRolls());

        assertThrows(NullPointerException.class, () ->{
            playerLudoStatsService.sumStats(null);
        });
    }

    @Test
    @Transactional
    public void top3MostLudoWins(){
        HashSet<PlayerLudoStats> allStats = new HashSet<>(playerLudoStatsService.findAll());

        Assertions.assertThat(playerLudoStatsService.top3MostLudoWins(allStats, "mostWins").get(0).getPlayer().getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(playerLudoStatsService.top3MostLudoWins(allStats, "mostWins").get(1).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_2);
        Assertions.assertThat(playerLudoStatsService.top3MostLudoWins(allStats, "mostWins").get(2).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_3);


        assertThrows(NullPointerException.class, () ->{
            playerLudoStatsService.sumStats(null);
        });
    }

    @Test
    @Transactional
    public void testTop3ByEatenTokens() {
        Set<PlayerLudoStats> allStats = new HashSet<>(playerLudoStatsService.findAll());

        Assertions.assertThat(playerLudoStatsService.top3MostLudoWins(allStats, "eatenTokens").get(0).getPlayer().getUser().getUsername()).isEqualTo(USERNAME);
        Assertions.assertThat(playerLudoStatsService.top3MostLudoWins(allStats, "eatenTokens").get(1).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_3);
        Assertions.assertThat(playerLudoStatsService.top3MostLudoWins(allStats, "eatenTokens").get(2).getPlayer().getUser().getUsername()).isEqualTo(USERNAME_2);


        assertThrows(NullPointerException.class, () ->{
            playerLudoStatsService.sumStats(null);
        });
    }

}
