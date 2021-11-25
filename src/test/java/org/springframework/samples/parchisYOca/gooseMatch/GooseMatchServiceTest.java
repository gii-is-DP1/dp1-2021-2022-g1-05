package org.springframework.samples.parchisYOca.gooseMatch;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseMatchServiceTest {

    protected final Integer MATCH_CODE_LENGTH = 6;
    protected final Integer INVALID_CODE_LENGTH = 5;

    @Autowired
    protected GooseMatchService gooseMatchService;
    @Autowired
    protected PlayerService playerService;

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

        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findGooseMatchById(addedMatch.getId()));
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
        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findGooseMatchById(addedMatch.getId()));
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
        GooseMatch match = new GooseMatch();
        match.setMatchCode(code);
        GooseMatch addedMatch = gooseMatchService.saveGooseMatchWithPlayer(match, addedPlayer, false);
        Assertions.assertThat(addedMatch).isEqualTo(gooseMatchService.findLobbyByUsername(user.getUsername()));

    }













    /*Integer idPrueba = 1;
	Long count = 0L;
	Integer size = 6;
	Integer invalidSize = 5;


    @Test
    @Transactional
    public void testAddCorrectMatch(){
        GooseMatch newMatch = new GooseMatch();
        //newMatch.setStartDate(new Date());
        String code = RandomStringGenerator.getRandomString(size);
        newMatch.setMatchCode(code);
        GooseMatch addedGooseMatch = gooseMatchService.save(newMatch);

        Assertions.assertThat(addedGooseMatch).isEqualTo(gooseMatchService.findGooseMatchByMatchCode(code).get());
    }


    @Test
    @Transactional
    public void testSaveMatchWithPlayer() {
        String code = RandomStringGenerator.getRandomString(size);
        GooseMatch match = new GooseMatch();
        match.setMatchCode(code);

        Player player = new Player();
        player.setEmail("carmen@domain.com");
        User user = new User();
        user.setUsername("Carmen");
        user.setPassword("Carmen1111");
        user.setEnabled(true);
        player.setUser(user);
        playerService.savePlayer(player);


        Player player = new Player();
        player.setEmail("pepe@gmail.com");
        User user = new User();
        user.setUsername("Pepe07");
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        Player addedplayer = playerService.savePlayer(player);

        gooseMatchService.saveGooseMatchWithPlayer(match, player, true);
        GooseMatch savedMatch = gooseMatchService.findGooseMatchByMatchCode(code).get();
        Assertions.assertThat(match).isEqualTo(savedMatch);
        Assertions.assertThat(savedMatch.getStats().size()).isEqualTo(1);
        List<PlayerGooseStats> savedMatchStats = new ArrayList<>(savedMatch.getStats());
        Assertions.assertThat(savedMatchStats.get(0).getIsOwner());
    }/*


	@Test
	public void testFindByID() {
		assertThrows(DataAccessException.class, ()->{
			gMatchService.findGooseMatchById(idPrueba);
		});
	}

	@Test
	public void testFindAll() {
		//la cantidad de partidas debe ser 0

        assertEquals(StreamSupport.stream(gMatchService
				.findAll()
				.spliterator(),false)
				.count(), count);
	}
	@Test
	@Transactional
	public void testSaveMatchPositivo() {
		String code = RandomStringGenerator.getRandomString(size);
		GooseMatch match = new GooseMatch();
		match.setMatchCode(code);
		assertDoesNotThrow(()->gMatchService.save(match));
	}
	@Test
	@Transactional
	public void testSaveMatchPlayerPositivo() {
		String code = RandomStringGenerator.getRandomString(size);
		GooseMatch match = new GooseMatch();
		match.setMatchCode(code);
		Player player = new Player();
	    player.setEmail("carmen@domain.com");
	    User user = new User();
	    user.setUsername("Carmen");
	    user.setPassword("Carmen1111");
	    user.setEnabled(true);
	    player.setUser(user);
		assertDoesNotThrow(()->gMatchService.saveGooseMatchWithPlayer(match, player, true));
	}
	@Test
	@Transactional
	public void testFindByMatchCode() {
		String code = RandomStringGenerator.getRandomString(size);
		GooseMatch match = new GooseMatch();
		match.setMatchCode(code);
		gMatchService.save(match);
		assertDoesNotThrow(()->gMatchService.findGooseMatchByMatchCode(code));
	}
	@Test
	public void testFindByMatchCodeNegative() {
		String code = RandomStringGenerator.getRandomString(invalidSize);
		assertThrows(DataAccessException.class, ()-> {
				gMatchService.findGooseMatchByMatchCode(code);
		});
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
	    String code = RandomStringGenerator.getRandomString(size);
		GooseMatch match = new GooseMatch();
		match.setMatchCode(code);
		gMatchService.save(match);
	    gMatchService.saveGooseMatchWithPlayer(match, player, false);
	    assertDoesNotThrow(()->gMatchService.findLobbyByUsername(user.getUsername()));

	}*/






}
