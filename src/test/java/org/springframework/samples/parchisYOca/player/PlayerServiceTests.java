package org.springframework.samples.parchisYOca.player;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PlayerServiceTests {

    private final Integer ID_TO_TEST = 1;
    private final Integer ID_TO_TEST_DISABLED = 7;
    private final String TEST_MATCHCODE = "ABCdef";

    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected GooseMatchService gooseMatchService;
    @Autowired
    protected PlayerGooseStatsService playerGooseStatsService;
    @Autowired
    protected LudoMatchService ludoMatchService;
    @Autowired
    protected PlayerLudoStatsService playerLudoStatsService;

    @Test
    @Transactional
    public void testInsertPlayer() {

        Player player = new Player();
        player.setEmail("pepe@gmail.com");
        User user = new User();
        user.setUsername("Pepe07");
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        Player addedplayer = playerService.savePlayer(player);
        assertThat(addedplayer).isEqualTo(playerService.findPlayerById(addedplayer.getId()).get());
    }

    @Test
    @Transactional
    public void testUpdatePlayer() {
        assertThat(playerService.findPlayerById(ID_TO_TEST).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST).get();
        player.setEmail("nuevoEmail@gmail.com");
        playerService.savePlayer(player);
        assertThat(playerService.findPlayerById(ID_TO_TEST).get().getEmail()).isEqualTo("nuevoEmail@gmail.com");
    }

    @Test
    @Transactional
    public void testInsertPlayerWithoutEmail(){
        Player player = new Player();
        User user = new User();
        user.setUsername("Pepe08");
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        assertThrows(ConstraintViolationException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    @Transactional
    public void testInsertPlayerWithoutUsername(){
        Player player = new Player();
        player.setEmail("pepeReturns@gmail.com");
        User user = new User();
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        assertThrows(JpaSystemException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    @Transactional
    public void testInsertPlayerWithWrongPassword(){
        Player player = new Player();
        player.setEmail("pepeIsUnstoppable@gmail.com");
        User user = new User();
        user.setUsername("Pepe09");
        user.setPassword("notSoGoodPassword");
        user.setEnabled(true);
        player.setUser(user);

        assertThrows(ConstraintViolationException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    @Transactional
    public void testAddWithDuplicatedName(){
        Player player = new Player();
        player.setEmail("pepeYouAreWrong@gmail.com");
        User user = new User();
        user.setUsername("ManuK");
        user.setPassword("SoGoodPassword3");
        user.setEnabled(true);
        player.setUser(user);


        assertThrows(DataIntegrityViolationException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    @Transactional
    public void testAddNullPlayer(){
        Player player = new Player();
        User user = new User();
        player.setUser(user);

        assertThrows(JpaSystemException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    @Transactional
    public void testDeletePlayer(){
        assertThat(playerService.findPlayerById(ID_TO_TEST).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST).get();
        playerService.delete(player);
        assertThat(playerService.findPlayerById(ID_TO_TEST).isEmpty());

    }

    @Test
    @Transactional
    public void testFindWinnerByGooseMatch(){
        assertThat(playerService.findPlayerById(ID_TO_TEST).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST).get();
        GooseMatch gooseMatch =  new GooseMatch();
        gooseMatch.setStartDate(new Date());
        gooseMatch.setEndDate(new Date());
        gooseMatch.setMatchCode(TEST_MATCHCODE);
        gooseMatchService.save(gooseMatch);
        PlayerGooseStats playerGooseStats = new PlayerGooseStats();
        playerGooseStats.setPlayer(player);
        playerGooseStats.setHasWon(1);
        playerGooseStats.setGooseMatch(gooseMatch);
        playerGooseStatsService.saveStats(playerGooseStats);

        assertThat(playerService.findWinnerByGooseMatchCode(TEST_MATCHCODE).get()).isEqualTo(player);
    }

    @Test
    @Transactional
    public void testFindWinnerByLudoMatch(){
        assertThat(playerService.findPlayerById(ID_TO_TEST).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST).get();
        LudoMatch ludoMatch =  new LudoMatch();
        ludoMatch.setStartDate(new Date());
        ludoMatch.setEndDate(new Date());
        ludoMatch.setMatchCode(TEST_MATCHCODE);
        ludoMatchService.save(ludoMatch);
        PlayerLudoStats playerLudoStats = new PlayerLudoStats();
        playerLudoStats.setPlayer(player);
        playerLudoStats.setHasWon(1);
        playerLudoStats.setLudoMatch(ludoMatch);
        playerLudoStatsService.saveStats(playerLudoStats);

        assertThat(playerService.findWinnerByLudoMatchCode(TEST_MATCHCODE).get()).isEqualTo(player);
    }

    @Test
    @Transactional
    public void testGetWithEmail(){
        assertThat(playerService.findPlayerByEmail("manu@gmail.com").isPresent());
    }

    @Test
    @Transactional
    public void testGetWithWrongEmail(){
        assertThat(playerService.findPlayerByEmail("asdasdasd@gmail.com").isEmpty());
    }

    @Test
    @Transactional
    public void testGetWithUsername(){
        assertThat(playerService.findPlayerByUsername("ManuK").isPresent());
    }

    @Test
    @Transactional
    public void testGetWithWrongUsername(){
        assertThat(playerService.findPlayerByUsername("").isEmpty());
    }

    @Test
    @Transactional
    public void testPaginationWithUsername(){
        assertThat(playerService.findPlayerById(ID_TO_TEST).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST).get();
        Integer numberOfPage = 0;
        Integer numberOfElements = 2;
        Pageable pageable = PageRequest.of(numberOfPage,numberOfElements);
        assertThat(playerService.findAllFilteringByUsername("", pageable).getContent().size()).isEqualTo(2);
        assertThat(playerService.findAllFilteringByUsername(player.getUser().getUsername(), pageable).getContent().contains(player.getUser().getUsername()));
    }

    @Test
    @Transactional
    public void testDisablePlayer(){
        assertThat(playerService.findPlayerById(ID_TO_TEST).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST).get();
        playerService.disable(player);
        assertThat(playerService.findPlayerById(ID_TO_TEST).get().getUser().isEnabled()).isEqualTo(false);
    }

    @Test
    @Transactional
    public void testEnablePlayer(){
        assertThat(playerService.findPlayerById(ID_TO_TEST_DISABLED).isPresent());
        Player player = playerService.findPlayerById(ID_TO_TEST_DISABLED).get();
        playerService.enable(player);
        assertThat(playerService.findPlayerById(ID_TO_TEST).get().getUser().isEnabled()).isEqualTo(true);
    }

}
