package org.springframework.samples.parchisYOca.gooseMatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockHttpSession;
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
@WebMvcTest(controllers = GooseMatchController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = WebSecurityConfigurer.class),
    excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
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
    private static final String LOLA = "Lola";
    private static final String LOLA_PSSWRD = "klasjldjl22";
    private static final Integer MATCH_ID = 1;
    private static final Integer LOBBY_ID = 2;
    private static final String LOBBY_CODE = "absdfg";
    private static final Boolean TRUE = true;
    private static final String MENSAJE = "You were the owner and left the game, so the lobby was closed!";
    private static final String MENSAJE_2 =  "You left the game!";
    private static final String MENSAJE_3 = "You left the lobby";
    private static final Integer PAGE_NUMBER = 0;
    private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 6;
    private static final Pageable PAGEABLE = PageRequest.of(PAGE_NUMBER, NUMBER_OF_ELEMENTS_PER_PAGE,Sort.by(Sort.Order.desc("startDate")));
    private static GooseMatch gMatch = TestDataGenerator.generateGooseLobby(LOBBY_CODE);
    private static  User LOLA_USER = TestDataGenerator.generateUser(LOLA, LOLA_PSSWRD);
    private static  Player LOLA_PLAYER = TestDataGenerator.generatePlayer(LOLA_USER);
    private static  PlayerGooseStats LOLA_STATS = TestDataGenerator.generateGooseStats(LOLA_PLAYER);
    private static  Set<PlayerGooseStats> STATS_SET = Set.of(LOLA_STATS);
    private static  GooseMatch LOLA_MATCH = TestDataGenerator.generateGooseMatch(LOBBY_CODE, STATS_SET, MATCH_ID);
    private static  Optional<GooseMatch> OP_LOLA_MATCH = Optional.of(LOLA_MATCH);
    private static Optional<GooseMatch> oGMatch = Optional.of(gMatch);
    public static Integer NUM_DICES=2;
    public static Integer NUM_DICES_SIDES=6;
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
        given(this.gooseMatchService.findLobbyByUsername(JAIME)).willReturn(oMatch);
        given(this.userService.findUserByUsername(PACO)).willReturn(oUserPaco);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(PACO, MATCH_ID))
            .willReturn(oPacoStats);
        given(this.gooseMatchService.findAllPaging(PAGEABLE)).willReturn(sGMatch);
        given(this.gooseMatchService.findAll()).willReturn(lGMatch);

    }

    @WithMockUser(value = PACO)
    @Test
    void testMatchCreationAlreadyInLobby() throws Exception {
    	given(this.gooseMatchService.findLobbyByUsername(PACO)).willReturn(oGMatch);
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        mockMvc.perform(get("/gooseMatches/new"))
            .andExpect(status().isFound())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
    @WithMockUser(value = PACO)
    @Test
    void testMatchCreation() throws Exception {
    	given(this.gooseMatchService.findLobbyByUsername(PACO)).willReturn(Optional.empty());
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        mockMvc.perform(get("/gooseMatches/new"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("/gooseMatches/lobby/??????"));
    }
    @WithMockUser(value = PACO)
    @Test
    void testMatchCreationNotLogged() throws Exception {
    	given(this.gooseMatchService.findLobbyByUsername(PACO)).willReturn(Optional.empty());
        given(this.userService.isAuthenticated()).willReturn(false);
        mockMvc.perform(get("/gooseMatches/new"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
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
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/lobby/"+MATCH_CODE));
    }
    @WithMockUser(value = JAIME)
    @Test
    void testJoinGooseMatchNotLoggedIn() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(false);
        mockMvc.perform(post("/gooseMatches/join").param("matchCode", MATCH_CODE))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testJoinGooseMatchUserInAnotherLobby() throws Exception {
    	LOLA_MATCH.setClosedLobby(0);
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(LOBBY_CODE);
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.of(newMatch));
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        given(this.playerService.findPlayerByUsername(LOLA)).willReturn(Optional.of(LOLA_PLAYER));
        mockMvc.perform(post("/gooseMatches/join").param("matchCode", MATCH_CODE))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message", "You are already at a lobby: "+LOBBY_CODE));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testJoinGooseMatchUserInMatchIsFull() throws Exception {
    	LOLA_MATCH.setClosedLobby(0);
    	LOLA_STATS.setId(1);
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(LOBBY_CODE);
    	PlayerGooseStats pStats2 = new PlayerGooseStats();
    	pStats2.setId(2);
    	PlayerGooseStats pStats3 = new PlayerGooseStats();
    	pStats3.setId(3);
    	PlayerGooseStats pStats4 = new PlayerGooseStats();
    	pStats4.setId(4);
    	Set<PlayerGooseStats> stats = Set.of(LOLA_STATS,pStats2,pStats3,pStats4);
    	newMatch.setStats(stats);
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.empty());
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        given(this.playerService.findPlayerByUsername(LOLA)).willReturn(Optional.of(LOLA_PLAYER));
        given(this.gooseMatchService.findGooseMatchByMatchCode(LOBBY_CODE)).willReturn(Optional.of(newMatch));
        mockMvc.perform(post("/gooseMatches/join").param("matchCode", LOBBY_CODE))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message", "The lobby is full!"));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testJoinGooseMatchUserInMatchIsNotFull() throws Exception {
    	LOLA_MATCH.setClosedLobby(0);
    	LOLA_STATS.setId(1);
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(LOBBY_CODE);
    	Set<PlayerGooseStats> stats = Set.of(LOLA_STATS);
    	newMatch.setStats(stats);
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.empty());
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        given(this.playerService.findPlayerByUsername(LOLA)).willReturn(Optional.of(LOLA_PLAYER));
        given(this.gooseMatchService.findGooseMatchByMatchCode(LOBBY_CODE)).willReturn(Optional.of(newMatch));
        mockMvc.perform(post("/gooseMatches/join").param("matchCode", LOBBY_CODE))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/lobby/"+LOBBY_CODE));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testJoinGooseMatchUserLobbyNotFound() throws Exception {
    	LOLA_MATCH.setClosedLobby(0);
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(LOBBY_CODE);
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.empty());
        given(this.gooseMatchService.findGooseMatchByMatchCode(LOBBY_CODE)).willReturn(Optional.empty());
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        given(this.playerService.findPlayerByUsername(LOLA)).willReturn(Optional.of(LOLA_PLAYER));
        mockMvc.perform(post("/gooseMatches/join").param("matchCode", LOBBY_CODE))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message", "Lobby not found!"));
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
        mockMvc.perform(get("/gooseMatches/lobby/"+MATCH_CODE))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
    @WithMockUser(value = "anonymousUser")
    @Test
    void testInitCreationLobbyMatchClosed() throws Exception {
    	String code =  "hdskar";
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(code);
    	newMatch.setClosedLobby(1);
    	given(this.gooseMatchService.findGooseMatchByMatchCode(code))
    	.willReturn(Optional.of(newMatch));
        MockHttpSession session =  new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
        		.get("/gooseMatches/lobby/"+code).session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
        assertThat(session.getAttribute("ownerLeft")).isEqualTo("The owner of the lobby left, so it was closed");
    }
    @WithMockUser(value = "anonymousUser")
    @Test
    void testInitCreationLobbyMatchNotClosed() throws Exception {
    	String code =  "hdskar";
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(code);
    	newMatch.setClosedLobby(0);
    	newMatch.setStartDate(new Date());
    	newMatch.setId(MATCH_ID);
    	given(this.gooseMatchService.findGooseMatchByMatchCode(code))
    	.willReturn(Optional.of(newMatch));
    	given(this.gooseMatchService.findGooseMatchByMatchCode(code))
    	.willReturn(Optional.of(newMatch));
        MockHttpSession session =  new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
        		.get("/gooseMatches/lobby/"+code).session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));
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
    @WithMockUser(value = LOLA)
    @Test
    void testLeaveMatchnNotOwner() throws Exception {
    	LOLA_STATS.setIsOwner(0);
    	LOLA_MATCH.setStartDate(new Date());
    	given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.of(LOLA_MATCH));
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        mockMvc.perform(get("/gooseMatches/matchLeft"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message",MENSAJE_2));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testLeaveMatchOwner() throws Exception {
    	LOLA_STATS.setIsOwner(1);
    	LOLA_MATCH.setStartDate(null);
    	given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.of(LOLA_MATCH));
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        mockMvc.perform(get("/gooseMatches/matchLeft"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message",MENSAJE));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testLeaveMatchInLobby() throws Exception {
    	LOLA_STATS.setIsOwner(0);
    	LOLA_MATCH.setStartDate(null);
    	given(this.gooseMatchService.findLobbyByUsername(LOLA)).willReturn(Optional.of(LOLA_MATCH));
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        mockMvc.perform(get("/gooseMatches/matchLeft"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message",MENSAJE_3));
    }
    @WithMockUser(value = PACO)
    @Test
    void testFilterGooseMatches() throws Exception {
    	String date = "2022-19-01";
        List<GooseMatch> lMatches = List.of(gMatch, LOLA_MATCH);
        Page<GooseMatch> sliMatches = new PageImpl<GooseMatch>(lMatches);
        given(this.gooseMatchService.findMatchesByStartDate(any(Date.class), any(Pageable.class))).willReturn(sliMatches);
    	mockMvc.perform(post("/gooseMatches")
    			.param("page", "0")
    			.param("filterBy", "startDate")
    			.param("date", date))
    	.andExpect(status().isOk())
    	.andExpect(view().name("matches/listGooseMatches"))
    	.andExpect(model().attribute("gooseMatches", sliMatches.getContent()));
    }
    @WithMockUser(value = PACO)
    @Test
    void testFilterGooseMatchesNotFilterBy() throws Exception {
    	String date = "2022-19-01";
        List<GooseMatch> lMatches = List.of(gMatch, LOLA_MATCH);
        Page<GooseMatch> sliMatches = new PageImpl<GooseMatch>(lMatches);
        given(this.gooseMatchService.findMatchesByEndDate(any(Date.class), any(Pageable.class))).willReturn(sliMatches);
    	mockMvc.perform(post("/gooseMatches")
    			.param("page", "0")
    			.param("filterBy", "whatever")
    			.param("date", date))
    	.andExpect(status().isOk())
    	.andExpect(view().name("matches/listGooseMatches"))
    	.andExpect(model().attribute("gooseMatches", sliMatches.getContent()));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testShowStats() throws Exception {
    	given(this.gooseMatchService.findGooseMatchById(MATCH_ID)).willReturn(OP_LOLA_MATCH);
    	mockMvc.perform(get("/gooseMatches/stats/"+MATCH_ID))
    	.andExpect(status().isOk())
    	.andExpect(view().name("stats/adminMatchStats"))
    	.andExpect(model().attribute("gooseStats", OP_LOLA_MATCH.get().getStats()));
    }
    @WithMockUser(value = LOLA)
    @Test
    void testInitCreationLobbyNotLoggedIn() throws Exception {
    	given(this.userService.isAuthenticated()).willReturn(false);
    	mockMvc.perform(get("/gooseMatches/lobby/"+MATCH_CODE))
    	.andExpect(status().is3xxRedirection())
    	.andExpect(redirectedUrl("/"));
    }
    @WithMockUser(value = LOLA)
    @Test
    void showMatchTestDicesAndMatchEndingWhenEveryOneLeft() throws Exception {
    	given(this.userService.isAuthenticated()).willReturn(true);
    	LOLA_MATCH.setId(MATCH_ID);
    	LOLA_MATCH.setStartDate(new Date());
    	LOLA_MATCH.setEndDate(new Date());
    	LOLA_STATS.setPlayerLeft(0);
    	LOLA_MATCH.setStats(STATS_SET);
    	int[] dices = new int[NUM_DICES+1];
        for (Integer i = 0; i<NUM_DICES; i++){
            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
        }
        given(this.gooseMatchService.findGooseMatchById(MATCH_ID))
        .willReturn(Optional.of(LOLA_MATCH));
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        given(this.gooseMatchService.findEveryoneExceptOneLeft(LOLA_MATCH)).willReturn(TRUE);
        given(this.playerGooseStatsService.findPlayerGooseStatsByUsername(LOLA)).willReturn(STATS_SET);
        given(this.playerGooseStatsService.sumStats(STATS_SET)).willReturn(LOLA_STATS);

    	 MockHttpSession session =  new MockHttpSession();
    	 session.setAttribute("dices", dices);
         MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
         		.get("/gooseMatches/"+MATCH_ID).session(session);
         mockMvc.perform(builder)
         .andExpect(status().isOk())
         .andExpect(model().attribute("firstDice", dices[0]))
         .andExpect(model().attribute("secondDice", dices[1]))
         .andExpect(model().attribute("sumDice", dices[2]))
         .andExpect(model().attribute("hasEnded",1))
         .andExpect(model().attribute("message", "Everyone except you left, so you won!"));
         assertThat(LOLA_STATS.getHasWon()).isEqualTo(1);
    }
    @WithMockUser(value = PACO)
    @Test
    void testFilterGooseMatchesNoDateValues() throws Exception {
    	String date = "";
        List<GooseMatch> lMatches = List.of(gMatch, LOLA_MATCH);
        Page<GooseMatch> sliMatches = new PageImpl<GooseMatch>(lMatches);
        given(this.gooseMatchService.findAllPaging(any(Pageable.class))).willReturn(sliMatches);
    	mockMvc.perform(post("/gooseMatches")
    			.param("page", "0")
    			.param("filterBy", "whatever")
    			.param("date", date))
    	.andExpect(status().isOk())
    	.andExpect(view().name("matches/listGooseMatches"))
    	.andExpect(model().attribute("gooseMatches", sliMatches.getContent()));
    }
    @WithMockUser(value = PACO)
    @Test
    void testInitCreationLobbyUserIsInAnotherLobby() throws Exception {
    	PlayerGooseStats newStats = TestDataGenerator.generateGooseStats(Jaime);
    	Set<PlayerGooseStats> statSet = Set.of(newStats);
    	GooseMatch newMatch = TestDataGenerator.generateGooseLobby(LOBBY_CODE);
    	newMatch.setId(MATCH_ID);
    	newMatch.setStartDate(null);
    	newMatch.setStats(statSet);
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        given(this.gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE))
        .willReturn(Optional.of(newMatch));
        given(this.userService.isAuthenticated()).willReturn(TRUE);
        mockMvc.perform(get("/gooseMatches/lobby/"+MATCH_CODE))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
    @WithMockUser(value = LOLA)
    @Test
    void showMatchTestDicesAndMatchEndingWhenNotEveryOneLeft() throws Exception {
    	given(this.userService.isAuthenticated()).willReturn(true);
    	LOLA_MATCH.setId(MATCH_ID);
    	LOLA_MATCH.setStartDate(new Date());
    	LOLA_MATCH.setEndDate(new Date());
    	LOLA_STATS.setPlayerLeft(0);
    	LOLA_MATCH.setStats(STATS_SET);
    	int[] dices = new int[NUM_DICES+1];
        for (Integer i = 0; i<NUM_DICES; i++){
            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
        }
        given(this.gooseMatchService.findGooseMatchById(MATCH_ID))
        .willReturn(Optional.of(LOLA_MATCH));
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
        given(this.gooseMatchService.findEveryoneExceptOneLeft(LOLA_MATCH)).willReturn(false);
        given(this.playerGooseStatsService.findPlayerGooseStatsByUsername(LOLA)).willReturn(STATS_SET);
        given(this.playerGooseStatsService.sumStats(STATS_SET)).willReturn(LOLA_STATS);

    	 MockHttpSession session =  new MockHttpSession();
    	 session.setAttribute("dices", dices);
         MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
         		.get("/gooseMatches/"+MATCH_ID).session(session);
         mockMvc.perform(builder)
         .andExpect(status().isOk())
         .andExpect(model().attribute("firstDice", dices[0]))
         .andExpect(model().attribute("secondDice", dices[1]))
         .andExpect(model().attribute("sumDice", dices[2]))
         .andExpect(model().attribute("hasEnded",1))
         .andExpect(model().attribute("message", "The game has ended!"));
         assertThat(LOLA_STATS.getHasWon()).isEqualTo(1);
    }
    @WithMockUser(value = LOLA)
    @Test
    void showMatchSpecialIsSet() throws Exception {
    	given(this.userService.isAuthenticated()).willReturn(true);
    	LOLA_MATCH.setId(MATCH_ID);
    	LOLA_MATCH.setStartDate(new Date());
    	LOLA_MATCH.setEndDate(null);
    	LOLA_STATS.setPlayerLeft(0);
    	LOLA_MATCH.setStats(STATS_SET);
    	int[] dices = new int[NUM_DICES+1];
        for (Integer i = 0; i<NUM_DICES; i++){
            dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
        }
        given(this.gooseMatchService.findGooseMatchById(MATCH_ID))
        .willReturn(Optional.of(LOLA_MATCH));
        given(this.playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(LOLA, MATCH_ID))
        .willReturn(Optional.of(LOLA_STATS));
    	 MockHttpSession session =  new MockHttpSession();
    	 session.setAttribute("dices", dices);
    	 String mensaje = "Mensaje especial";
    	 session.setAttribute("especial", mensaje);
         MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
         		.get("/gooseMatches/"+MATCH_ID).session(session);
         mockMvc.perform(builder)
         .andExpect(status().isOk())
         .andExpect(model().attribute("firstDice", dices[0]))
         .andExpect(model().attribute("secondDice", dices[1]))
         .andExpect(model().attribute("sumDice", dices[2]))
         .andExpect(model().attribute("message", mensaje));
    }
}

