package org.springframework.samples.parchisYOca.gooseMatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.samples.parchisYOca.achievement.AchievementService;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardService;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = GooseMatchController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
public class GooseMatchControllerTest {
	@Autowired
    private MockMvc mockMvc;
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
	@MockBean
	private AchievementService achivService;

	private static final Integer JAIME_ID = 5;
	private static final Integer PACO_ID = 6;
	private static final Integer IS_OWNER =1;
	private static final String MATCH_CODE = "abcdfg";
	private static final String JAIME = "Jaime";
	private static final String PACO = "Paco";
	private static final Integer MATCH_ID = 1;
	private static final Boolean TRUE = true;
	private static final String MENSAJE = "You were the owner and left the game, so the lobby was closed!";
	private static final Integer PAGE_NUMBER = 0;
	private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 6;
	private static final Pageable PAGEABLE = PageRequest.of(PAGE_NUMBER, NUMBER_OF_ELEMENTS_PER_PAGE,Sort.by(Sort.Order.desc("startDate")));
	private Player Jaime;
	private Player Paco;
	private GooseMatch match;
	private PlayerGooseStats jaimeStats;
	private PlayerGooseStats pacoStats;

	@BeforeEach
	void setup() {
		Jaime = new Player();
		User userJaime = new User();
		userJaime.setUsername(JAIME);
		userJaime.setPassword(JAIME);
		Optional<User> oUserJaime = Optional.of(userJaime);
		Jaime.setUser(userJaime);
		Jaime.setEmail("jaime@domain.com");
		Jaime.setId(JAIME_ID);
		jaimeStats = new PlayerGooseStats();
		jaimeStats.setIsOwner(0);
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
		Set<PlayerGooseStats> players = new HashSet<PlayerGooseStats>();
		players.add(jaimeStats);
		players.add(pacoStats);
		Optional<Player> oJaime = Optional.of(Jaime);
 		Optional<Player> oPaco = Optional.of(Paco);
		Optional<PlayerGooseStats> oPacoStats = Optional.of(pacoStats);
		given(this.playerService.findPlayerById(JAIME_ID)).willReturn(oJaime);
		given(this.playerService.findPlayerById(PACO_ID)).willReturn(oPaco);
		given(this.playerService.findPlayerByUsername(JAIME)).willReturn(oJaime);
		given(this.playerService.findPlayerByUsername(PACO)).willReturn(oPaco);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(PACO, MATCH_ID))
		.willReturn(oPacoStats);
		match = new GooseMatch();
		match.setMatchCode(MATCH_CODE);
		match.setId(MATCH_ID);
		match.setStats(players);
		List<GooseMatch> lGMatch = List.of(match);
		Slice<GooseMatch> sGMatch = new SliceImpl<GooseMatch>(lGMatch);
		Optional<GooseMatch> oMatch = Optional.of(match);
		given(this.gooseMatchService.findGooseMatchById(MATCH_ID)).willReturn(oMatch);
		given(this.gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE)).willReturn(oMatch);
		given(this.userService.findUserByUsername(JAIME)).willReturn(oUserJaime);
		given(this.userService.findUserByUsername(PACO)).willReturn(oUserPaco);
		given(this.gooseMatchService.findLobbyByUsername(JAIME)).willReturn(oMatch);
		given(this.gooseMatchService.findLobbyByUsername(PACO)).willReturn(oMatch);
		given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(PACO, MATCH_ID))
		.willReturn(oPacoStats);
		given(this.gooseMatchService.findAllPaging(PAGEABLE)).willReturn(sGMatch);
		given(this.gooseMatchService.findAll()).willReturn(lGMatch);

	}

	@WithMockUser(value = PACO)
	@Test
	void testMatchCreation() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/new"))
		.andExpect(status().isFound())
		.andExpect(status().is3xxRedirection());
	}
	@WithMockUser(value = "spring")
	@Test
	void testJoinGooseMatchForm() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/join"))
		.andExpect(status().isOk())
		.andExpect(view().name("matches/joinMatchForm"));
	}
	@WithMockUser(value = JAIME)
	@Test
	void testJoinGooseMatch() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(post("/gooseMatches/join").param("matchCode", MATCH_CODE))
		.andExpect(status().isForbidden());
	}
	@WithMockUser(value = PACO)
	@Test
	void testInitCreationLobby() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/lobby/"+MATCH_CODE))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("matchId"))
		.andExpect(model().attributeExists("match"))
		.andExpect(model().attributeExists("matchCode"))
		.andExpect(model().attributeExists("stats"));
	}
	@WithMockUser(value = "spring")
	@Test
	void testInitCreationLobbyPlayerNotInLobby() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/lobby/"+MATCH_CODE))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));
	}
	@WithMockUser(value = "anonymousUser")
	@Test
	void testInitCreationLobbyPlayerNotLoggedIn() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(false);
		mockMvc.perform(get("/gooseMatches/lobby/"+MATCH_CODE))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));
	}
	@WithMockUser(value = PACO)
	@Test
	void testShowMatch() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/"+MATCH_ID))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("chips"))
		.andExpect(model().attributeExists("stats"))
		.andExpect(model().hasNoErrors());
	}
	@WithMockUser(value = "anonymousUser")
	@Test
	void testShowMatchNotLoggedIn() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(false);
		mockMvc.perform(get("/gooseMatches/"+MATCH_ID))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/"));
	}
	@WithMockUser(value = "spring")
	@Test
	void testListadoPartidas() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches").param("page", String.valueOf(PAGE_NUMBER)))
		.andExpect(status().isOk())
		.andExpect(view().name("matches/listGooseMatches"));
	}
	@WithMockUser(value = "spring")
	@Test
	void testCloseMatch() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/close/"+MATCH_ID))
		.andExpect(status().is3xxRedirection())
		.andExpect(redirectedUrl("/gooseMatches?page=0"));
	}
	@WithMockUser(value = PACO)
	@Test
	void testLeaveMatch() throws Exception {
		given(this.userService.isAuthenticated()).willReturn(TRUE);
		mockMvc.perform(get("/gooseMatches/matchLeft"))
		.andExpect(status().isOk())
		.andExpect(model().attribute("message",MENSAJE));
	}

}
