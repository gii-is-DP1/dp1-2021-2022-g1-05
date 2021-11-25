package org.springframework.samples.parchisYOca.gooseMatch;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.*;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;

public class GooseMatchServiceTest {
	@Autowired
	private GooseMatchService gMatchService;
	@Autowired
    protected PlayerService playerService;
	
	 
	
	Integer idPrueba = 1;
	Long count = 0L;
	Integer size = 6;
	Integer invalidSize = 5;
	
	
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
		assertDoesNotThrow(()->gMatchService.saveGooseMatchWithPlayer(match, player));
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
	    gMatchService.saveGooseMatchWithPlayer(match, player);
	    assertDoesNotThrow(()->gMatchService.findLobbyByUsername(user.getUsername()));
	    
	}
}
