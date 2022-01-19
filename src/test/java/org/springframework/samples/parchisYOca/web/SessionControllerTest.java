package org.springframework.samples.parchisYOca.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SessionController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
public class SessionControllerTest {
	@Autowired
    private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@MockBean
	private UserService userService;
	@MockBean
    private PlayerGooseStatsService playerGooseStatsService;
	@MockBean
    private PlayerLudoStatsService playerLudoStatsService;

	private static final Boolean FROMGOOSE = true;
	private static final Boolean FROMLUDO = true;
	private static final Boolean TRUE = true;
	private static final Integer MATCH_ID = 1;
	private static final String LAURA = "Laura";
	private static final Integer LAURA_INGAMEID = 2;
	private static final Integer LAURA_ID = 7;
	private static final String MATCH_CODE = "abcdfe";
	private static final Integer HAS_TURN = 1;
	private static final Integer DOESNT_HAVE_TURN = 0;
	private Player Laura;
	private GooseMatch match;
	private PlayerGooseStats lauraGooseStats;
	private PlayerLudoStats lauraLudoStats;

	@BeforeEach
	void setup() {
		Laura = new Player();
		User userLaura = new User();
		userLaura.setUsername(LAURA);
		userLaura.setPassword(LAURA);
		Optional<User> oUserLaura = Optional.of(userLaura);
		Laura.setUser(userLaura);
		Laura.setEmail("jaime@domain.com");
		Laura.setId(LAURA_ID);
		Set<PlayerGooseStats> players = new HashSet<PlayerGooseStats>();
		players.add(lauraGooseStats);
		match = new GooseMatch();
		match.setMatchCode(MATCH_CODE);
		match.setId(MATCH_ID);
		match.setStats(players);


	}

	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesGoose() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		lauraGooseStats = new PlayerGooseStats();
		lauraGooseStats.setPlayer(Laura);
		lauraGooseStats.setHasTurn(HAS_TURN);
		Optional<PlayerGooseStats> oLauraGooseStats = Optional.of(lauraGooseStats);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraGooseStats);
		MockHttpSession sessionGoose = new MockHttpSession();
		sessionGoose.setAttribute("fromGoose",FROMGOOSE);
		sessionGoose.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionGoose);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/gooseInGame/dicesRolled"));
		assertThat(sessionGoose.getAttribute("dices")).isNotNull();
	}

	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesLudo() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		lauraLudoStats = new PlayerLudoStats();
		lauraLudoStats.setPlayer(Laura);
		lauraLudoStats.setHasTurn(HAS_TURN);
		Optional<PlayerLudoStats> oLauraLudoStats = Optional.of(lauraLudoStats);
		given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraLudoStats);
		MockHttpSession sessionLudo = new MockHttpSession();
		sessionLudo.setAttribute("fromLudo",FROMLUDO);
		sessionLudo.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionLudo);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/ludoInGame/dicesRolled"));
		assertThat(sessionLudo.getAttribute("dices")).isNotNull();

	}
	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesGooseNotLoggedIn() throws Exception {
		lauraGooseStats = new PlayerGooseStats();
		lauraGooseStats.setPlayer(Laura);
		lauraGooseStats.setHasTurn(HAS_TURN);
		Optional<PlayerGooseStats> oLauraGooseStats = Optional.of(lauraGooseStats);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraGooseStats);
		MockHttpSession sessionGoose = new MockHttpSession();
		sessionGoose.setAttribute("fromGoose",FROMGOOSE);
		sessionGoose.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionGoose);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));

	}
	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesLudoNotLoggedIn() throws Exception {
		lauraLudoStats = new PlayerLudoStats();
		lauraLudoStats.setPlayer(Laura);
		lauraLudoStats.setHasTurn(HAS_TURN);
		Optional<PlayerLudoStats> oLauraLudoStats = Optional.of(lauraLudoStats);
		given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraLudoStats);
		MockHttpSession sessionLudo = new MockHttpSession();
		sessionLudo.setAttribute("fromLudo",FROMLUDO);
		sessionLudo.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionLudo);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));

	}
	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesGooseDoesntHaveTurn() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		lauraGooseStats = new PlayerGooseStats();
		lauraGooseStats.setPlayer(Laura);
		lauraGooseStats.setHasTurn(DOESNT_HAVE_TURN);
		Optional<PlayerGooseStats> oLauraGooseStats = Optional.of(lauraGooseStats);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraGooseStats);
		MockHttpSession sessionGoose = new MockHttpSession();
		sessionGoose.setAttribute("fromGoose",FROMGOOSE);
		sessionGoose.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionGoose);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));
		assertThat(sessionGoose.getAttribute("dices")).isNull();
	}
	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesLudoDoesntHaveTurn() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		lauraLudoStats = new PlayerLudoStats();
		lauraLudoStats.setPlayer(Laura);
		lauraLudoStats.setHasTurn(DOESNT_HAVE_TURN);
		Optional<PlayerLudoStats> oLauraLudoStats = Optional.of(lauraLudoStats);
		given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraLudoStats);
		MockHttpSession sessionLudo = new MockHttpSession();
		sessionLudo.setAttribute("fromLudo",FROMLUDO);
		sessionLudo.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionLudo);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/ludoMatches/"+MATCH_ID));
		assertThat(sessionLudo.getAttribute("dices")).isNull();

	}
}
