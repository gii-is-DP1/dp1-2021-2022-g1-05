package org.springframework.samples.parchisYOca.gooseBoard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = GooseBoardController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
public class GooseBoardControllerTest {
	@Autowired
    private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@MockBean
    private GooseMatchService gooseMatchService;
	@MockBean
    private PlayerService playerService;
	@MockBean
    private PlayerGooseStatsService playerGooseStatsService;
	@MockBean
    private GooseBoardService gooseBoardService;
	@MockBean
    private GooseChipService gooseChipService;
	@MockBean
    private UserService userService;


	private static final Boolean LOGGED_IN =true;
	private static final Boolean TRUE =true;
	private static final Integer JAIME_ID = 5;
	private static final Integer JAIME_INGAMEID = 0;
	private static final Integer PACO_ID = 6;
	private static final Integer PACO_INGAMEID = 1;
	private static final Integer IS_OWNER =1;
	private static final Integer LAURA_ID = 7;
	private static final Integer LAURA_INGAMEID = 2;
	private static final Integer CARMEN_ID = 8;
	private static final Integer CARMEN_INGAMEID = 3;
	private static final String MATCH_CODE = "abcdfg";
	private static final String JAIME = "Jaime";
	private static final String PACO = "Paco";
	private static final String LAURA = "Laura";
	private static final String CARMEN = "Carmen";
	private static final Integer MATCH_ID = 1;
	private static final int[] DICES = {1,2,3};
	private Player Jaime;
	private Player Paco;
	private Player Laura;
	private Player Carmen;
	private GooseMatch match;
	private PlayerGooseStats jaimeStats;
	private PlayerGooseStats pacoStats;
	private PlayerGooseStats lauraStats;
	private PlayerGooseStats carmenStats;

	@BeforeEach
	void setup() {
		/*La mayor parte de esto es inutil pero no me apetece ver ahora
		 que sobra y que no*/
		Jaime = new Player();
		User userJaime = new User();
		userJaime.setUsername(JAIME);
		userJaime.setPassword(JAIME);
		Optional<User> oUserJaime = Optional.of(userJaime);
		Jaime.setUser(userJaime);
		Jaime.setEmail("jaime@domain.com");
		Jaime.setId(JAIME_ID);
		jaimeStats = new PlayerGooseStats();
		jaimeStats.setPlayer(Jaime);
		Paco = new Player();
		User userPaco = new User();
		userPaco.setUsername(PACO);
		userPaco.setPassword(PACO);
		Optional<User> oUserPaco = Optional.of(userPaco);
		Paco.setUser(userPaco);
		Paco.setEmail("Paco@domain.com");
		Paco.setId(PACO_ID);
		pacoStats = new PlayerGooseStats();
		pacoStats.setPlayer(Paco);
		pacoStats.setIsOwner(IS_OWNER);
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
		Carmen = new Player();
		User userCarmen = new User();
		userCarmen.setUsername(CARMEN);
		userCarmen.setPassword(CARMEN);
		Optional<User> oUserCarmen = Optional.of(userCarmen);
		Carmen.setUser(userCarmen);
		Carmen.setEmail("jaime@domain.com");
		Carmen.setId(CARMEN_ID);
		carmenStats = new PlayerGooseStats();
		carmenStats.setPlayer(Carmen);
		Set<PlayerGooseStats> players = new HashSet<PlayerGooseStats>();
		players.add(jaimeStats);
		players.add(pacoStats);
		players.add(lauraStats);
		players.add(carmenStats);
		Optional<Player> oJaime = Optional.of(Jaime);
 		Optional<Player> oPaco = Optional.of(Paco);
		Optional<PlayerGooseStats> oPacoStats = Optional.of(pacoStats);
		Optional<PlayerGooseStats> oJaimeStats = Optional.of(jaimeStats);
		Optional<PlayerGooseStats> oLauraStats = Optional.of(lauraStats);
		Optional<PlayerGooseStats> oCarmenStats = Optional.of(carmenStats);
		given(this.playerService.findPlayerById(JAIME_ID)).willReturn(oJaime);
		given(this.playerService.findPlayerById(PACO_ID)).willReturn(oPaco);
		given(this.playerService.findPlayerByUsername(JAIME)).willReturn(oJaime);
		given(this.playerService.findPlayerByUsername(PACO)).willReturn(oPaco);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(PACO, MATCH_ID))
		.willReturn(oPacoStats);
		given(this.userService.isAuthenticated()).willReturn(LOGGED_IN);
		given(this.userService.findUserByUsername(PACO)).willReturn(oUserPaco);
		given(this.userService.findUserByUsername(JAIME)).willReturn(oUserJaime);
		given(this.userService.findUserByUsername(LAURA)).willReturn(oUserLaura);
		given(this.userService.findUserByUsername(CARMEN)).willReturn(oUserCarmen);
		given(this.playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(PACO_INGAMEID, MATCH_ID))
		.willReturn(oPacoStats);
		given(this.playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(JAIME_INGAMEID, MATCH_ID))
		.willReturn(oJaimeStats);
		given(this.playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(LAURA_INGAMEID, MATCH_ID))
		.willReturn(oLauraStats);
		given(this.playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(CARMEN_INGAMEID, MATCH_ID))
		.willReturn(oCarmenStats);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(JAIME, MATCH_ID))
		.willReturn(oJaimeStats);
		match = new GooseMatch();
		match.setMatchCode(MATCH_CODE);
		match.setId(MATCH_ID);
		match.setStats(players);

	}
	@WithMockUser(value = JAIME)
	@Test
	void testGooseDicesRolled() throws Exception {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("dices", DICES);
		session.setAttribute("fromGoose",TRUE);
		session.setAttribute("matchId", MATCH_ID);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
                .session(session);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection());
	}

}
