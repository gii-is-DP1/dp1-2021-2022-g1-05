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
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.samples.parchisYOca.web.SessionController;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
	private Player Laura;
	private GooseMatch match;
	private PlayerGooseStats lauraStats;
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
		lauraStats = new PlayerGooseStats();
		lauraStats.setPlayer(Laura);
		lauraStats.setHasTurn(HAS_TURN);
		Set<PlayerGooseStats> players = new HashSet<PlayerGooseStats>();
		players.add(lauraStats);
		Optional<PlayerGooseStats> oLauraStats = Optional.of(lauraStats);
		match = new GooseMatch();
		match.setMatchCode(MATCH_CODE);
		match.setId(MATCH_ID);
		match.setStats(players);
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LAURA, MATCH_ID))
		.willReturn(oLauraStats);
		
	}
	
	@WithMockUser(value = LAURA)
	@Test
	void testRollDicesGoose() throws Exception {
		MockHttpSession sessionGoose = new MockHttpSession();
		sessionGoose.setAttribute("fromGoose",FROMGOOSE);
		sessionGoose.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/session/rolldices")
                .session(sessionGoose);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/gooseInGame/dicesRolled"));
		
	}
}
