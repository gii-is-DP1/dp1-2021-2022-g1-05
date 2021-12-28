package org.springframework.samples.parchisYOca.ludoMatch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class LudoMatchServiceTests {

    protected final Integer MATCH_CODE_LENGTH = 6;
    protected final Integer INVALID_CODE_LENGTH = 5;

    @Autowired
    private PlayerLudoStatsService playerLudoStatsService;
    @Autowired
    private LudoMatchService ludoMatchService;
    @Autowired
    private PlayerService playerService;

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

        assertThat(addedMatch).isEqualTo(ludoMatchService.findludoMatchById(addedMatch.getId()));
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
        assertThat(addedMatch).isEqualTo(ludoMatchService.findludoMatchById(addedMatch.getId()));
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

}

