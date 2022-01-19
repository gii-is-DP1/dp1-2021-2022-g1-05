package org.springframework.samples.parchisYOca.ludoMatch;

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
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LudoMatchServiceTests {

    protected final Integer MATCH_CODE_LENGTH = 6;
    protected final Integer INVALID_CODE_LENGTH = 5;
    protected final String USERNAME = "ManuK";
    protected final String MATCH_CODE_1 = "09yupo";
    protected final String MATCH_CODE_2 = "JANORL";
    protected final String MATCH_CODE_3 = "123456";
    protected final String MATCH_CODE_4 = "9floep";

    @Autowired
    private PlayerLudoStatsService playerLudoStatsService;
    @Autowired
    private LudoMatchService ludoMatchService;
    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void addMatchesWithDates(){
        //Populate with matches
        LudoMatch newMatch1 = new LudoMatch();
        String matchCode1 = MATCH_CODE_1;
        newMatch1.setMatchCode(matchCode1);
        newMatch1.setStartDate(new Date());
        LudoMatch newMatch2 = new LudoMatch();
        String matchCode2 = MATCH_CODE_2;
        newMatch2.setMatchCode(matchCode2);
        newMatch2.setEndDate(new Date());
        ludoMatchService.save(newMatch2);
        LudoMatch newMatch3 = new LudoMatch();
        String matchCode3 = MATCH_CODE_3;
        newMatch3.setMatchCode(matchCode3);
        Date date1 = new GregorianCalendar(2020, Calendar.FEBRUARY, 11).getTime();
        newMatch3.setStartDate(date1);
        ludoMatchService.save(newMatch3);
        LudoMatch newMatch4 = new LudoMatch();
        String matchCode4 = MATCH_CODE_4;
        newMatch4.setMatchCode(matchCode4);
        Date date2 = new GregorianCalendar(2021, Calendar.MAY, 23).getTime();
        newMatch4.setEndDate(date2);
        ludoMatchService.save(newMatch4);

        //Populate stats
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        PlayerLudoStats pls1 = new PlayerLudoStats();
        pls1.setPlayer(player);
        pls1.setHasWon(1);
        pls1.setEatenTokens(20);
        pls1.setScoredTokens(2);
        pls1.setInGameId(1);
        playerLudoStatsService.saveStats(pls1);
        PlayerLudoStats pls2 = new PlayerLudoStats();
        pls2.setPlayer(player);
        pls2.setHasWon(1);
        pls2.setInGameId(3);
        playerLudoStatsService.saveStats(pls2);
        PlayerLudoStats pls3 = new PlayerLudoStats();
        pls3.setPlayer(player);
        pls3.setTakeOuts(30);
        pls3.setEatenTokens(10);
        pls3.setWalkedSquares(100);
        pls3.setInGameId(1);
        playerLudoStatsService.saveStats(pls3);

        Set<PlayerLudoStats> statsOfGame1 = Set.of(pls1, pls2, pls3);
        newMatch1.setStats(statsOfGame1);
        ludoMatchService.save(newMatch1);
    }

    @Test
    @Transactional
    public void getWithMatchCode(){
        assertThat(ludoMatchService.findludoMatchByMatchCode("111111").isPresent());
    }

    @Test
    @Transactional
    public void getWithWrongMatchCode(){
        assertThat(ludoMatchService.findludoMatchByMatchCode("").isEmpty());
    }

    @Test
    @Transactional
    public void testAddMatch(){
        LudoMatch newMatch = new LudoMatch();
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        newMatch.setMatchCode(matchCode);
        newMatch.setStartDate(new Date());

        LudoMatch addedMatch = ludoMatchService.save(newMatch);

        assertThat(addedMatch).isEqualTo(ludoMatchService.findludoMatchById(addedMatch.getId()).get());
    }

    @Test
    @Transactional
    public void testAddMatchWithOwner(){
        LudoMatch newMatch = new LudoMatch();
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

        LudoMatch addedMatch = ludoMatchService.saveludoMatchWithPlayer(newMatch, addedPlayer, true);;
        assertThat(addedMatch).isEqualTo(ludoMatchService.findludoMatchById(addedMatch.getId()).get());
        assertThat(addedMatch.getStats().size()).isEqualTo(1);
        List<PlayerLudoStats> savedMatchStats = new ArrayList<>(addedMatch.getStats());
        assertThat(savedMatchStats.get(0).getIsOwner());

    }

    @Test
    @Transactional
    public void testFindByMatchCode() {
        String code = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        LudoMatch match = new LudoMatch();
        match.setMatchCode(code);

        LudoMatch addedMatch = ludoMatchService.save(match);
        Assertions.assertThat(addedMatch).isEqualTo(ludoMatchService.findludoMatchByMatchCode(code).get());

        String code2 = RandomStringGenerator.getRandomString(INVALID_CODE_LENGTH);
        assertThat(ludoMatchService.findludoMatchByMatchCode(code2).isEmpty());
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
        LudoMatch match = new LudoMatch();
        match.setMatchCode(code);
        LudoMatch addedMatch = ludoMatchService.saveludoMatchWithPlayer(match, addedPlayer, false);
        Assertions.assertThat(addedMatch).isEqualTo(ludoMatchService.findLobbyByUsername(user.getUsername()).get());
    }
    @Test
    public void testFindEveryoneExceptOneLeft() {
        LudoMatch newMatch = new LudoMatch();
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
        LudoMatch addedMatch = ludoMatchService.saveludoMatchWithPlayer(newMatch, addedPlayer, true);
        LudoMatch addedMatch2=ludoMatchService.saveludoMatchWithPlayer(addedMatch, addedPlayer2, false);

        List<PlayerLudoStats> aux=new ArrayList<>(addedMatch2.getStats());
        PlayerLudoStats pgs=aux.get(0);
        pgs.setPlayerLeft(1);
        pgs.setHasTurn(Integer.MIN_VALUE);
        playerLudoStatsService.saveStats(pgs);
        Assertions.assertThat(ludoMatchService.findEveryoneExceptOneLeft(addedMatch2)).isEqualTo(true);
    }

    @Test
    @Transactional
    public void testRemoveLudoStatsFromGame() {
        Integer statsToRemoveId = 1;
        LudoMatch lm = ludoMatchService.findludoMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(lm.getStats().size()).isEqualTo(3);
        ludoMatchService.removeLudoStatsFromGame(playerLudoStatsService.findById(statsToRemoveId).get(),lm.getId());
        Assertions.assertThat(lm.getStats().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    public void testRemoveAllLudoStatsFromGame() {
        LudoMatch lm = ludoMatchService.findludoMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(lm.getStats().size()).isEqualTo(3);
        ludoMatchService.removeAllLudoStatsFromGame(lm.getId());
        Assertions.assertThat(lm.getStats().size()).isEqualTo(0);
    }

    @Test
    public void testFindByStartDate() {
        Date date1 = new GregorianCalendar(2020, Calendar.FEBRUARY, 10).getTime();
        Date date2 = new GregorianCalendar(2020, Calendar.MAY, 10).getTime();

        Assertions.assertThat(ludoMatchService.findMatchesByStartDate(date1, PageRequest.of(0,10)).getTotalElements()).isEqualTo(2);
        Assertions.assertThat(ludoMatchService.findMatchesByStartDate(date2, PageRequest.of(0,10)).getTotalElements()).isEqualTo(1);
        Assertions.assertThat(ludoMatchService.findMatchesByStartDate(date1, PageRequest.of(0,1)).getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(ludoMatchService.findMatchesByStartDate(date1, PageRequest.of(0,1)).getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testFindByEndDate() {
        Date date1 = new GregorianCalendar(2021, Calendar.MAY, 22).getTime();
        Date date2 = new GregorianCalendar(2021, Calendar.AUGUST, 10).getTime();

        Assertions.assertThat(ludoMatchService.findMatchesByEndDate(date1, PageRequest.of(0,10)).getTotalElements()).isEqualTo(2);
        Assertions.assertThat(ludoMatchService.findMatchesByEndDate(date2, PageRequest.of(0,10)).getTotalElements()).isEqualTo(1);
        Assertions.assertThat(ludoMatchService.findMatchesByEndDate(date1, PageRequest.of(0,1)).getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(ludoMatchService.findMatchesByEndDate(date1, PageRequest.of(0,1)).getTotalElements()).isEqualTo(2);
    }

    @Test
    public void testFindEndedLudoMatches() {
        Integer numberOfEndedMatches = 2;
        Assertions.assertThat(ludoMatchService.findEndedLudoMatches().size()).isEqualTo(numberOfEndedMatches);
    }

    @Test
    public void testFindMatchByStats() {
        PlayerLudoStats stats = playerLudoStatsService.findById(1).get();
        LudoMatch ludoMatch = ludoMatchService.findludoMatchByMatchCode(MATCH_CODE_1).get();
        Assertions.assertThat(ludoMatchService.findMatchByPlayerLudoStats(stats).get()).isEqualTo(ludoMatch);
    }

    @Test
    public void testFindLudoMatchesOfPlayer() {
        Assertions.assertThat(playerService.findPlayerByUsername(USERNAME).isPresent());
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        LudoMatch ludoMatch = new LudoMatch();
        ludoMatch.setMatchCode("abcdef");
        PlayerLudoStats pls1 = new PlayerLudoStats();
        pls1.setPlayer(player);
        PlayerLudoStats savedStats = playerLudoStatsService.saveStats(pls1);
        Set<PlayerLudoStats> statsSet = Set.of(savedStats);
        ludoMatch.setStats(statsSet);
        ludoMatchService.save(ludoMatch);

        Assertions.assertThat(ludoMatchService.findMatchesByUsername(USERNAME).size()).isEqualTo(2);
        Assertions.assertThat(ludoMatchService.findMatchesByUsername("").size()).isEqualTo(0);
        Assertions.assertThat(ludoMatchService.findEndedLudoMatches().contains(ludoMatch));
    }

    @Test
    public void testFindLudoMatchesByUsernameWithPaging() {
        Assertions.assertThat(playerService.findPlayerByUsername(USERNAME).isPresent());
        Player player = playerService.findPlayerByUsername(USERNAME).get();
        LudoMatch ludoMatch1 = ludoMatchService.findludoMatchByMatchCode(MATCH_CODE_1).get();
        LudoMatch ludoMatch2 = ludoMatchService.findludoMatchByMatchCode(MATCH_CODE_2).get();
        LudoMatch ludoMatch3 = ludoMatchService.findludoMatchByMatchCode(MATCH_CODE_3).get();
        PlayerLudoStats pls2 = new PlayerLudoStats();
        pls2.setPlayer(player);
        PlayerLudoStats savedStats2 = playerLudoStatsService.saveStats(pls2);
        PlayerLudoStats pls3 = new PlayerLudoStats();
        pls3.setPlayer(player);
        PlayerLudoStats savedStats3 = playerLudoStatsService.saveStats(pls3);
        Pageable pageable = PageRequest.of(0,2);
        Set<PlayerLudoStats> statsSet2 = new HashSet<>();
        statsSet2.add(savedStats2);
        Set<PlayerLudoStats> statsSet3 = new HashSet<>();
        statsSet3.add(savedStats3);
        ludoMatch2.setStats(statsSet2);
        ludoMatch3.setStats(statsSet3);
        ludoMatchService.save(ludoMatch2);
        ludoMatchService.save(ludoMatch3);

        Assertions.assertThat(ludoMatchService.findMatchesByUsernameWithPaging(USERNAME, pageable).getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(ludoMatchService.findMatchesByUsernameWithPaging(USERNAME, pageable).getContent().contains(ludoMatch1));
        Assertions.assertThat(ludoMatchService.findMatchesByUsernameWithPaging("", pageable).getNumberOfElements()).isEqualTo(0);
    }

}

