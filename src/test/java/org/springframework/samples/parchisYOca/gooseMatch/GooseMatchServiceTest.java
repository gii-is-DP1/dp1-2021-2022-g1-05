package org.springframework.samples.parchisYOca.gooseMatch;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GooseMatchServiceTest {

    protected final Integer MATCH_CODE_LENGTH = 6;
    protected final Integer INVALID_CODE_LENGTH = 5;
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
        //Populate with matches
        GooseMatch newMatch1 = new GooseMatch();
        String matchCode1 = MATCH_CODE_1;
        newMatch1.setMatchCode(matchCode1);
        newMatch1.setStartDate(new Date());
        GooseMatch newMatch2 = new GooseMatch();
        String matchCode2 = MATCH_CODE_2;
        newMatch2.setMatchCode(matchCode2);
        newMatch2.setEndDate(new Date());
        gooseMatchService.save(newMatch2);
        GooseMatch newMatch3 = new GooseMatch();
        String matchCode3 = MATCH_CODE_3;
        newMatch3.setMatchCode(matchCode3);
        Date date1 = new GregorianCalendar(2020, Calendar.FEBRUARY, 11).getTime();
        newMatch3.setStartDate(date1);
        gooseMatchService.save(newMatch3);
        GooseMatch newMatch4 = new GooseMatch();
        String matchCode4 = MATCH_CODE_4;
        newMatch4.setMatchCode(matchCode4);
        Date date2 = new GregorianCalendar(2021, Calendar.MAY, 23).getTime();
        newMatch4.setEndDate(date2);
        gooseMatchService.save(newMatch4);

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

        Set<PlayerGooseStats> statsOfGame1 = Set.of(pgs1, pgs2, pgs3);
        newMatch1.setStats(statsOfGame1);
        gooseMatchService.save(newMatch1);

    }


    @Test
    @Transactional
    public void testGetWithMatchCode(){
        GooseMatch newMatch = new GooseMatch();
        String matchCode = "abcdef";
        newMatch.setMatchCode(matchCode);
        GooseMatch addedMatch = gooseMatchService.save(newMatch);
        Assertions.assertThat(gooseMatchService.findGooseMatchByMatchCode(matchCode).isPresent());
        Assertions.assertThat(gooseMatchService.findGooseMatchByMatchCode(matchCode).get()).isEqualTo(addedMatch);
    }

    @Test
    @Transactional
    public void testGetWithWrongMatchCode(){
        Assertions.assertThat(gooseMatchService.findGooseMatchByMatchCode("").isEmpty());
    }

    @Test
    @Transactional
    public void testAddMatch(){
        GooseMatch newMatch = new GooseMatch();
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        newMatch.setMatchCode(matchCode);
        newMatch.setStartDate(new Date());

        GooseMatch addedMatch = gooseMatchService.save(newMatch);

        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findGooseMatchById(addedMatch.getId()).get());
    }

    @Test
    @Transactional
    public void testAddMatchWithOwner(){
        GooseMatch newMatch = new GooseMatch();
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        newMatch.setMatchCode(matchCode);
        newMatch.setStartDate(new Date());

        Player player = new Player();
        player.setEmail("carmen@domain.com");
        User user = new User();
        user.setUsername("Carmen");
        user.setPassword("Carmen1111");
        user.setEnabled(true);
        player.setUser(user);

        Player addedPlayer = playerService.savePlayer(player);

        GooseMatch addedMatch = gooseMatchService.saveGooseMatchWithPlayer(newMatch, addedPlayer, true);;
        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findGooseMatchById(addedMatch.getId()).get());
        Assertions.assertThat(addedMatch.getStats().size()).isEqualTo(1);
        List<PlayerGooseStats> savedMatchStats = new ArrayList<>(addedMatch.getStats());
        Assertions.assertThat(savedMatchStats.get(0).getIsOwner());

    }

    @Test
    @Transactional
    public void testFindByMatchCode() {
        String code = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        GooseMatch match = new GooseMatch();
        match.setMatchCode(code);

        GooseMatch addedMatch = gooseMatchService.save(match);
        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findGooseMatchByMatchCode(code).get());

        String code2 = RandomStringGenerator.getRandomString(INVALID_CODE_LENGTH);
        Assertions.assertThat(gooseMatchService.findGooseMatchByMatchCode(code2).isEmpty());
    }

    @Test
    @Transactional
    public void testFindLobbyByUserName() {
        Player player = new Player();
        player.setEmail("carmen@domain.com");
        User user = new User();
        user.setUsername("Carmen");
        user.setPassword("Carmen1111");
        user.setEnabled(true);
        player.setUser(user);

        Player addedPlayer = playerService.savePlayer(player);

        String code = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        GooseMatch match = new GooseMatch();
        match.setMatchCode(code);
        GooseMatch addedMatch = gooseMatchService.saveGooseMatchWithPlayer(match, addedPlayer, false);
        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findLobbyByUsername(user.getUsername()).get());
    }

    @Test
    public void testFindEveryoneExceptOneLeft() {
        GooseMatch newMatch = new GooseMatch();
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        newMatch.setMatchCode(matchCode);
        newMatch.setStartDate(new Date());
        //Player 1
        Player player = new Player();
        player.setEmail("carmen@domain.com");
        User user = new User();
        user.setUsername("Carmen");
        user.setPassword("Carmen1111");
        user.setEnabled(true);
        player.setUser(user);
        Player addedPlayer = playerService.savePlayer(player);
        //Player2
        Player player2 = new Player();
        player2.setEmail("carmeeeeen@domain.com");
        User user2 = new User();
        user2.setUsername("Carmen12");
        user2.setPassword("Carmen111112");
        user2.setEnabled(true);
        player2.setUser(user2);
        Player addedPlayer2 = playerService.savePlayer(player2);
        GooseMatch addedMatch = gooseMatchService.saveGooseMatchWithPlayer(newMatch, addedPlayer, true);
        GooseMatch addedMatch2=gooseMatchService.saveGooseMatchWithPlayer(addedMatch, addedPlayer2, false);

        List<PlayerGooseStats> aux=new ArrayList<>(addedMatch2.getStats());
        PlayerGooseStats pgs=aux.get(0);
        pgs.setPlayerLeft(1);
        pgs.setHasTurn(Integer.MIN_VALUE);
        playerGooseStatsService.saveStats(pgs);
        Assertions.assertThat(gooseMatchService.findEveryoneExceptOneLeft(addedMatch2)).isEqualTo(true);
    }

    @Test
    public void testFindEndedGooseMatches() {
        Integer numberOfEndedMatches = 2;
        Assertions.assertThat(gooseMatchService.findEndedGooseMatches().size()).isEqualTo(numberOfEndedMatches);
    }

    @Test
    public void testFindGooseMatchesOfPlayer() {
        Assertions.assertThat(playerService.findPlayerByUsername(USERNAME).isPresent());
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        GooseMatch gooseMatch = new GooseMatch();
        gooseMatch.setMatchCode("abcdef");
        PlayerGooseStats pgs1 = new PlayerGooseStats();
        pgs1.setPlayer(player);
        PlayerGooseStats savedStats = playerGooseStatsService.saveStats(pgs1);
        Set<PlayerGooseStats> statsSet = Set.of(savedStats);
        gooseMatch.setStats(statsSet);
        gooseMatchService.save(gooseMatch);

        Assertions.assertThat(gooseMatchService.findMatchesByUsername(USERNAME).size()).isEqualTo(2);
        Assertions.assertThat(gooseMatchService.findMatchesByUsername("").size()).isEqualTo(0);
        Assertions.assertThat(gooseMatchService.findEndedGooseMatches().contains(gooseMatch));
    }

    @Test
    public void testFindMatchByStats() {
        PlayerGooseStats stats = playerGooseStatsService.findById(1).get();
        GooseMatch gooseMatch = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(gooseMatchService.findMatchByPlayerGooseStats(stats).get()).isEqualTo(gooseMatch);
    }

   @Test
    public void testFindGooseMatchesByUsernameWithPaging() {
        Assertions.assertThat(playerService.findPlayerByUsername(USERNAME).isPresent());
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        GooseMatch gooseMatch1 = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_1).get();
        GooseMatch gooseMatch2 = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_2).get();
        GooseMatch gooseMatch3 = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_3).get();
        PlayerGooseStats pgs2 = new PlayerGooseStats();
        pgs2.setPlayer(player);
        PlayerGooseStats savedStats2 = playerGooseStatsService.saveStats(pgs2);
        PlayerGooseStats pgs3 = new PlayerGooseStats();
        pgs3.setPlayer(player);
        PlayerGooseStats savedStats3 = playerGooseStatsService.saveStats(pgs3);
        Pageable pageable = PageRequest.of(0,2);
       Set<PlayerGooseStats> statsSet2 = new HashSet<>();
       statsSet2.add(savedStats2);
       Set<PlayerGooseStats> statsSet3 = new HashSet<>();
       statsSet3.add(savedStats3);
       gooseMatch2.setStats(statsSet2);
       gooseMatch3.setStats(statsSet3);
       gooseMatchService.save(gooseMatch2);
       gooseMatchService.save(gooseMatch3);

        Assertions.assertThat(gooseMatchService.findMatchesByUsernameWithPaging(USERNAME, pageable).getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(gooseMatchService.findMatchesByUsernameWithPaging(USERNAME, pageable).getContent().contains(gooseMatch1));
        Assertions.assertThat(gooseMatchService.findMatchesByUsernameWithPaging("", pageable).getNumberOfElements()).isEqualTo(0);
    }

    @Test
    public void testFindByStartDate() {
        Date date1 = new GregorianCalendar(2020, Calendar.FEBRUARY, 10).getTime();
        Date date2 = new GregorianCalendar(2020, Calendar.MAY, 10).getTime();

        Assertions.assertThat(gooseMatchService.findMatchesByStartDate(date1, PageRequest.of(0,10)).getTotalElements()).isEqualTo(2);
        Assertions.assertThat(gooseMatchService.findMatchesByStartDate(date2, PageRequest.of(0,10)).getTotalElements()).isEqualTo(1);
        Assertions.assertThat(gooseMatchService.findMatchesByStartDate(date1, PageRequest.of(0,1)).getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(gooseMatchService.findMatchesByStartDate(date1, PageRequest.of(0,1)).getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testFindByEndDate() {
        Date date1 = new GregorianCalendar(2021, Calendar.MAY, 22).getTime();
        Date date2 = new GregorianCalendar(2021, Calendar.AUGUST, 10).getTime();

        Assertions.assertThat(gooseMatchService.findMatchesByEndDate(date1, PageRequest.of(0,10)).getTotalElements()).isEqualTo(2);
        Assertions.assertThat(gooseMatchService.findMatchesByEndDate(date2, PageRequest.of(0,10)).getTotalElements()).isEqualTo(1);
        Assertions.assertThat(gooseMatchService.findMatchesByEndDate(date1, PageRequest.of(0,1)).getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(gooseMatchService.findMatchesByEndDate(date1, PageRequest.of(0,1)).getTotalElements()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void testRemoveGooseStatsFromGame() {
        Integer statsToRemoveId = 1;
        GooseMatch gm = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(gm.getStats().size()).isEqualTo(3);
        Collection<PlayerGooseStats> pgsColl = playerGooseStatsService.findPlayerGooseStatsByGame(gm.getId());
        gooseMatchService.removeGooseStatsFromGame(playerGooseStatsService.findById(statsToRemoveId).get(),pgsColl,gm.getId());
        Assertions.assertThat(gm.getStats().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void testRemoveAllGooseStatsFromGame() {
        GooseMatch gm = gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(gm.getStats().size()).isEqualTo(3);
        gooseMatchService.removeAllGooseStatsFromGame(gm.getId());
        Assertions.assertThat(gm.getStats().size()).isEqualTo(0);
    }

}
