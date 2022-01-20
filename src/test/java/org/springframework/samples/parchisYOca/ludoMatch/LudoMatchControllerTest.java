package org.springframework.samples.parchisYOca.ludoMatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.samples.parchisYOca.achievement.AchievementService;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoardService;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import testDataGenerator.TestDataGenerator;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = LudoMatchController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class LudoMatchControllerTest {
	private static final String NAME = "name";
	private static final String PSSWRD = "lajfdlkaj22";
	private static final String NAME2 = "name2";
	private static final String PSSWRD2 = "lajfdlkaej2";
	private static final String CODE = "asddUQ";
	private static final String ANOTHER_CODE = "ADDSAo";
	private static final String ATRBT = "match";
	private static final String VALUE =  "You are already at a lobby: " + ANOTHER_CODE;
	private static final String VALUE_2 = "The lobby is full!";
	private static final String VALUE_3 = "Lobby not found!";
	private static final String VALUE_4 = "The owner of the lobby left, so it was closed";
	private static final String VALUE_5 = "Everyone except you left, so you won!";
	private static final String VALUE_6 = "The game has ended!";
	private static final String MENSAJE_DE_PRUEBA = "AAAAAAAAAAAAAAAAAAAA";
	private static final String LOBBY_CLOSED_OWNER = "You were the owner and left the game, so the lobby was closed!";
	private static final String LEFT_NOT_OWNER = "You left the lobby";
	private static final String LEFT_MATCH = "You left the game!";
	private static final User user = TestDataGenerator.generateUser(NAME, PSSWRD);
	private static final Player player = TestDataGenerator.generatePlayer(user);
	private static final String VIEW_NAME = "matches/joinMatchForm";
	private static final String VIEW_NAME_MATCHES = "matches/ludoMatchLobby";
	private static final String VIEW_MATCHES_STATS = "stats/adminMatchStats";
	private static final String VIEW_LISTADO = "matches/listLudoMatches";
	private static final User user2 = TestDataGenerator.generateUser(NAME2, PSSWRD2);
	private static final Player player2 = TestDataGenerator.generatePlayer(user2);
	private static final Integer MATCH_ID = 1;
	public static Integer NUM_DICES=2;
    public static Integer NUM_DICES_SIDES=6;

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
	   @WithMockUser(value = NAME)
	   @Test
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
	   @WithMockUser(value = NAME)
	   @Test
	   void testPostJoinLudoMatchMatchNotFound() throws Exception {
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.empty());
		   given(this.playerService.findPlayerByUsername(NAME))
		   .willReturn(Optional.of(player));
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.empty());
		   mockMvc.perform(post("/ludoMatches/join")
				   .param("matchCode", CODE))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("message", VALUE_3))
		   .andExpect(view().name(VIEW_NAME));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testInitCreationLobbyClosedLobby() throws Exception {
		   LudoMatch match =  new LudoMatch();
		   match.setClosedLobby(1);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(match));
		   MockHttpSession session = new MockHttpSession();
		   MockHttpServletRequestBuilder builder =
				   MockMvcRequestBuilders.get("/ludoMatches/lobby/"+CODE)
				   .session(session);
		   mockMvc.perform(builder)
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
		   assertThat(session.getAttribute("ownerLeft")).isEqualTo(VALUE_4);
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testInitCreationLobbyJoinsMatch() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setClosedLobby(0);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(match));
		   mockMvc.perform(get("/ludoMatches/lobby/"+CODE))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/ludoMatches/"+match.getId()));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testInitCreationLobbyNotLoggedIn() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setClosedLobby(0);
		   match.setStartDate(null);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(match));
		   given(this.userService.isAuthenticated()).willReturn(false);
		   mockMvc.perform(get("/ludoMatches/lobby/"+CODE))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testInitCreationLobbyInLobby() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setClosedLobby(0);
		   match.setStartDate(null);
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   stat.setIsOwner(1);
		   Set<PlayerLudoStats> stats = Set.of(stat);
		   match.setStats(stats);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(match));
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, match.getId()))
		   .willReturn(Optional.of(stat));
		   mockMvc.perform(get("/ludoMatches/lobby/"+CODE))
		   .andExpect(status().isOk())
		   .andExpect(view().name(VIEW_NAME_MATCHES))
		   .andExpect(model().attribute("stats", match.getStats()))
		   .andExpect(model().attribute("matchCode", CODE))
		   .andExpect(model().attribute("match", match))
		   .andExpect(model().attribute("matchId", match.getId()));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testInitCreationLobbyNotInLobby() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setClosedLobby(0);
		   match.setStartDate(null);
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player2);
		   Set<PlayerLudoStats> stats =Set.of(stat);
		   match.setStats(stats);
		   given(this.ludoMatchService.findludoMatchByMatchCode(CODE))
		   .willReturn(Optional.of(match));
		   given(this.userService.isAuthenticated()).willReturn(true);
		   mockMvc.perform(get("/ludoMatches/lobby/"+CODE))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testShowMatchNotLoggedIn() throws Exception {
		   given(this.userService.isAuthenticated()).willReturn(false);
		   mockMvc.perform(get("/ludoMatches/"+MATCH_ID))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testShowMatchLastPlayerLeft() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   Set<PlayerLudoStats> stats = Set.of(stat);
		   stat.setPlayerLeft(0);
		   match.setId(MATCH_ID);
		   match.setStartDate(null);
		   match.setStats(stats);
		   match.setEndDate(new Date());
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findludoMatchById(MATCH_ID))
		   .willReturn(Optional.of(match));
		   MockHttpSession session = new MockHttpSession();
		   int[] dices = new int[NUM_DICES+1];
	        for (Integer i = 0; i<NUM_DICES; i++){
	            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
	        }
	        List<LudoChip> chips = new ArrayList<LudoChip>();
	        given(this.ludoChipService.findChipsByMatchId(MATCH_ID))
	        .willReturn(chips);
	        given(this.ludoChipService.checkOcuppied(chips))
	        .willReturn(new ArrayList<LudoChip>());
	        given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, MATCH_ID))
	        .willReturn(Optional.of(stat));
	        given(this.ludoMatchService.findEveryoneExceptOneLeft(match)).willReturn(true);
	       session.setAttribute("dices", dices);
	       given(this.playerLudoStatsService.sumStats(stats)).willReturn(stat);
		   MockHttpServletRequestBuilder builder =
				   MockMvcRequestBuilders.get("/ludoMatches/"+MATCH_ID)
				   .session(session);
		   mockMvc.perform(builder)
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("message", VALUE_5))
		   .andExpect(view().name("matches/ludoMatch"))
		   .andExpect(model().attribute("firstDice", dices[0]))
		   .andExpect(model().attribute("secondDice", dices[1]))
		   .andExpect(model().attribute("sumDice", dices[2]));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testShowMatchMatchHasEnded() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   Set<PlayerLudoStats> stats = Set.of(stat);
		   stat.setPlayerLeft(0);
		   match.setId(MATCH_ID);
		   match.setStartDate(null);
		   match.setStats(stats);
		   match.setEndDate(new Date());
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findludoMatchById(MATCH_ID))
		   .willReturn(Optional.of(match));
		   MockHttpSession session = new MockHttpSession();
		   int[] dices = new int[NUM_DICES+1];
	        for (Integer i = 0; i<NUM_DICES; i++){
	            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
	        }
	        List<LudoChip> chips = new ArrayList<LudoChip>();
	        given(this.ludoChipService.findChipsByMatchId(MATCH_ID))
	        .willReturn(chips);
	        given(this.ludoChipService.checkOcuppied(chips))
	        .willReturn(new ArrayList<LudoChip>());
	        given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, MATCH_ID))
	        .willReturn(Optional.of(stat));
	        given(this.ludoMatchService.findEveryoneExceptOneLeft(match)).willReturn(false);
	       session.setAttribute("dices", dices);
	       given(this.playerLudoStatsService.sumStats(stats)).willReturn(stat);
		   MockHttpServletRequestBuilder builder =
				   MockMvcRequestBuilders.get("/ludoMatches/"+MATCH_ID)
				   .session(session);
		   mockMvc.perform(builder)
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("message", VALUE_6))
		   .andExpect(view().name("matches/ludoMatch"))
		   .andExpect(model().attribute("firstDice", dices[0]))
		   .andExpect(model().attribute("secondDice", dices[1]))
		   .andExpect(model().attribute("sumDice", dices[2]));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testShowMatch() throws Exception {
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   stat.setHasTurn(2);
		   Set<PlayerLudoStats> stats = Set.of(stat);
		   stat.setPlayerLeft(0);
		   match.setId(MATCH_ID);
		   match.setStartDate(null);
		   match.setStats(stats);
		   match.setEndDate(null);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findludoMatchById(MATCH_ID))
		   .willReturn(Optional.of(match));
		   MockHttpSession session = new MockHttpSession();
		   int[] dices = new int[NUM_DICES+1];
	        for (Integer i = 0; i<NUM_DICES; i++){
	            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
	        }
	        List<LudoChip> chips = new ArrayList<LudoChip>();
	        given(this.ludoChipService.findChipsByMatchId(MATCH_ID))
	        .willReturn(chips);
	        given(this.ludoChipService.checkOcuppied(chips))
	        .willReturn(new ArrayList<LudoChip>());
	        given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, MATCH_ID))
	        .willReturn(Optional.of(stat));
	        given(this.ludoMatchService.findEveryoneExceptOneLeft(match)).willReturn(false);
	       session.setAttribute("dices", dices);
	       session.setAttribute("especial", MENSAJE_DE_PRUEBA);
	       given(this.playerLudoStatsService.sumStats(stats)).willReturn(stat);
		   MockHttpServletRequestBuilder builder =
				   MockMvcRequestBuilders.get("/ludoMatches/"+MATCH_ID)
				   .session(session);
		   mockMvc.perform(builder)
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("message", MENSAJE_DE_PRUEBA))
		   .andExpect(model().attribute("hasTurn", stat.getHasTurn()))
		   .andExpect(view().name("matches/ludoMatch"))
		   .andExpect(model().attribute("firstDice", dices[0]))
		   .andExpect(model().attribute("secondDice", dices[1]))
		   .andExpect(model().attribute("sumDice", dices[2]));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testCloseMatch() throws Exception {
		   LudoMatch match = new LudoMatch();
		   given(this.ludoMatchService.findludoMatchById(MATCH_ID))
		   .willReturn(Optional.of(match));
		   mockMvc.perform(get("/ludoMatches/close/"+MATCH_ID))
		   .andExpect(status().is3xxRedirection())
		   .andExpect(redirectedUrl("/ludoMatches?page=0"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testShowStats() throws Exception {
		   LudoMatch match = new LudoMatch();
		   Set<PlayerLudoStats> stats = new HashSet<PlayerLudoStats>();
		   for(int i=0; i<4; ++i) {
			   User user = TestDataGenerator.generateUser(String.valueOf(i), PSSWRD+1);
			   Player player = TestDataGenerator.generatePlayer(user);
			   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
			   stats.add(stat);
		   }
		   match.setStats(stats);
		   given(this.ludoMatchService.findludoMatchById(MATCH_ID))
		   .willReturn(Optional.of(match));
		   mockMvc.perform(get("/ludoMatches/stats/"+MATCH_ID))
		   .andExpect(status().isOk())
		   .andExpect(view().name(VIEW_MATCHES_STATS))
		   .andExpect(model().attribute("ludoStats", stats));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testListadoPartidas() throws Exception {
		   List<LudoMatch> matches = List.of();
		   Page<LudoMatch> slice = new PageImpl<LudoMatch>(matches);
		   given(this.ludoMatchService.findAllPaging(any(Pageable.class)))
		   .willReturn(slice);
		   mockMvc.perform(get("/ludoMatches")
				   .param("page", "0"))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("ludoMatches", slice.getContent()))
		   .andExpect(view().name(VIEW_LISTADO));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testListadoPartidasFiltradoStartDate() throws Exception {
		   String date = "2022-09-09";
		   LudoMatch match = new LudoMatch();
		   List<LudoMatch> matches = List.of(match);
		   Page<LudoMatch> slice = new PageImpl<LudoMatch>(matches);
		   given(this.ludoMatchService.findMatchesByStartDate(any(Date.class),any(Pageable.class)))
		   .willReturn(slice);
		   mockMvc.perform(post("/ludoMatches")
				   .param("page", "0")
				   .param("filterBy", "startDate")
				   .param("date", date))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("ludoMatches", slice.getContent()))
		   .andExpect(view().name(VIEW_LISTADO));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testListadoPartidasFiltradoEndDate() throws Exception {
		   String date = "2022-09-09";
		   LudoMatch match = new LudoMatch();
		   List<LudoMatch> matches = List.of(match);
		   Page<LudoMatch> slice = new PageImpl<LudoMatch>(matches);
		   given(this.ludoMatchService.findMatchesByEndDate(any(Date.class),any(Pageable.class)))
		   .willReturn(slice);
		   mockMvc.perform(post("/ludoMatches")
				   .param("page", "0")
				   .param("filterBy", "endDate")
				   .param("date", date))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("ludoMatches", slice.getContent()))
		   .andExpect(view().name(VIEW_LISTADO));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testListadoPartidasFiltradoNoFiltering() throws Exception {
		   String date = "";
		   LudoMatch match = new LudoMatch();
		   List<LudoMatch> matches = List.of(match);
		   Page<LudoMatch> slice = new PageImpl<LudoMatch>(matches);
		   given(this.ludoMatchService.findAllPaging(any(Pageable.class)))
		   .willReturn(slice);
		   mockMvc.perform(post("/ludoMatches")
				   .param("page", "0")
				   .param("filterBy", "endDate")
				   .param("date", date))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("ludoMatches", slice.getContent()))
		   .andExpect(view().name(VIEW_LISTADO));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testLeaveMatch() throws Exception {
		   given(this.userService.isAuthenticated()).willReturn(false);
		   mockMvc.perform(get("/ludoMatches/matchLeft"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("welcome"));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testLeaveMatchLoggedInIsowner() throws Exception {
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   stat.setIsOwner(1);
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setStartDate(null);
		   match.setId(MATCH_ID);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.of(match));
		   given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, MATCH_ID))
		   .willReturn(Optional.of(stat));
		   mockMvc.perform(get("/ludoMatches/matchLeft"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("welcome"))
		   .andExpect(model().attribute("message", LOBBY_CLOSED_OWNER));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testLeaveMatchLoggedInNotOwner() throws Exception {
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   stat.setIsOwner(0);
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setStartDate(null);
		   match.setId(MATCH_ID);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.of(match));
		   given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, MATCH_ID))
		   .willReturn(Optional.of(stat));
		   mockMvc.perform(get("/ludoMatches/matchLeft"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("welcome"))
		   .andExpect(model().attribute("message", LEFT_NOT_OWNER));
	   }
	   @WithMockUser(value = NAME)
	   @Test
	   void testLeaveMatchGame() throws Exception {
		   PlayerLudoStats stat = TestDataGenerator.generatePlayerLudoStats(player);
		   stat.setIsOwner(0);
		   LudoMatch match = TestDataGenerator.generateLudoMatch(CODE);
		   match.setStartDate(new Date());
		   match.setId(MATCH_ID);
		   given(this.userService.isAuthenticated()).willReturn(true);
		   given(this.ludoMatchService.findLobbyByUsername(NAME))
		   .willReturn(Optional.of(match));
		   given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(NAME, MATCH_ID))
		   .willReturn(Optional.of(stat));
		   mockMvc.perform(get("/ludoMatches/matchLeft"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("welcome"))
		   .andExpect(model().attribute("message", LEFT_MATCH));
	   }
}
