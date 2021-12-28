package org.springframework.samples.parchisYOca.ludoMatch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
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

    @Disabled("Da stack overflow error")
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
        assertThat(addedMatch).isEqualTo(ludoMatchService.findLobbyByUsername(user.getUsername()));

    }

}

