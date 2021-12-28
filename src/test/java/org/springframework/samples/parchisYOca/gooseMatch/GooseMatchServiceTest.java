package org.springframework.samples.parchisYOca.gooseMatch;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseMatchServiceTest {

    protected final Integer MATCH_CODE_LENGTH = 6;
    protected final Integer INVALID_CODE_LENGTH = 5;

    @Autowired
    protected GooseMatchService gooseMatchService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected PlayerGooseStatsService playerGooseStatsService;

    @Test
    @Transactional
    public void getWithMatchCode(){
        Assertions.assertThat(gooseMatchService.findGooseMatchByMatchCode("111111").isPresent());
    }

    @Test
    @Transactional
    public void getWithWrongMatchCode(){
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


}
