package org.springframework.samples.parchisYOca.ludoMatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.parchisYOca.achievement.AchievementService;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoardService;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import testDataGenerator.TestDataGenerator;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@WebMvcTest(controllers = LudoMatchController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class LudoMatchControllerTest {
	private static final String NAME = "name";
	private static final String PSSWRD = "lajfdlkaj22";
	private static final String CODE = "asddUQ";
	private static final String ANOTHER_CODE = "ADDSAo";
	private static final String ATRBT = "match";
	private static final String VALUE =  "You are already at a lobby: " + ANOTHER_CODE;
	private static final String VALUE_2 = "The lobby is full!";
	private static final User user = TestDataGenerator.generateUser(NAME, PSSWRD);
	private static final Player player = TestDataGenerator.generatePlayer(user);
	private static final String VIEW_NAME = "matches/joinMatchForm";
	
	@Autowired
    private MockMvc mockMvc;
	
	@MockBean
	private LudoChipService ludoChipService;
	@MockBean
    private LudoMatchService ludoMatchService;
	@MockBean
    private LudoBoardService ludoBoardService;
	@MockBean
    private PlayerService playerService;
	@MockBean
    private PlayerLudoStatsService playerLudoStatsService;
	@MockBean
    private UserService userService;
	@MockBean
    private AchievementService achievementService;
	@MockBean
	private AuthoritiesService authoritiesService;

	   @WithMockUser(value = "spring")
	   @Test
	   void testCreateMatchNotLoggedIn() throws Exception {
		   given(this.userService.isAuthenticated()).willReturn(false);
		   mockMvc.perform(get("/ludoMatches/new"))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testCreateMatchLoggedIn() throws Exception {
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.empty());
		   mockMvc.perform(get("/ludoMatches/new"))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrlPattern("/ludoMatches/lobby/??????"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testCreateMatchPlayerInAnotherLobby() throws Exception {
		   LudoMatch newMatch =  TestDataGenerator.generateLudoMatch(CODE);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.of(newMatch));
		   mockMvc.perform(get("/ludoMatches/new"))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testGetJoinLudoMatchForm() throws Exception {
		   mockMvc.perform(get("/ludoMatches/join"))
		   .andExpect(status().isOk())
		   .andExpect(view().name(VIEW_NAME))
		   .andExpect(model().attributeExists(ATRBT));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testPostJoinLudoMatchFormNotLoggedIn() throws Exception {
		   given(this.userService.isAuthenticated()).willReturn(false);
		   mockMvc.perform(post("/ludoMatches/join")
				   .param("matchCode", CODE))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testPostJoinLudoMatchFormJoinsMatch() throws Exception {
		   LudoMatch newMatch = TestDataGenerator.generateLudoMatch(CODE);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.of(newMatch));
		   mockMvc.perform(post("/ludoMatches/join")
				   .param("matchCode", CODE))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/ludoMatches/lobby/"+CODE));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testPostJoinLudoMatchFormAlreadyAtALobby() throws Exception {
		   LudoMatch newMatch = TestDataGenerator.generateLudoMatch(ANOTHER_CODE);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.of(newMatch));
		   mockMvc.perform(post("/ludoMatches/join")
				   .param("matchCode", CODE))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("message", VALUE))
		   .andExpect(view().name(VIEW_NAME));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testPostJoinLudoMatchNotAlreadyInAMatch() throws Exception {
		   Set<PlayerLudoStats> stats = new HashSet<PlayerLudoStats>();
		   LudoMatch newMatch = TestDataGenerator.generateLudoMatch(CODE);
		   newMatch.setStats(stats);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(newMatch));
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.empty());
		   mockMvc.perform(post("/ludoMatches/join")
				   .param("matchCode", CODE))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/ludoMatches/lobby/"+CODE));
	   }
	   void testPostJoinLudoMatchFullMatch() throws Exception {
		   Set<PlayerLudoStats> stats = new HashSet<PlayerLudoStats>();
		   for(int i=0; i<4; ++i) {
			   User user = TestDataGenerator.generateUser(String.valueOf(i), PSSWRD+1);
			   Player player = TestDataGenerator.generatePlayer(user);
			   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
			   stats.add(stat);
		   }
		   LudoMatch newMatch = TestDataGenerator.generateLudoMatch(CODE);
		   newMatch.setStats(stats);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(newMatch));
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.empty());
		   mockMvc.perform(post("/ludoMatches/join")
				   .param("matchCode", CODE))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("message", VALUE_2))
		   .andExpect(view().name(VIEW_NAME));
	   }
}
