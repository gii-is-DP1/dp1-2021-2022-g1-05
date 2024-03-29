package org.springframework.samples.parchisYOca.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.Authorities;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import testDataGenerator.TestDataGenerator;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlayerController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class PlayerControllerTests {
	private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 6;
    private static final int ID = 1;
    private static final String PASSWORD = "1234567";
    private static final Integer PAGE_NUMBER = 0;
    private static final String USERNAME = "Juan";
    private static final String EMAIL = "Juant@gmail.com";
    private static final String AUTH = "admin";
    private static final String NEW_PASSWORD = "ABCde34";
    private static final String NEW_EMAIL = "jjjuan@domain.com";
    private static final String MESSAGE = "Player successfully disabled!";
    private static final String MESSAGE2 = "Player successfully enabled!";
    private static final String MESSAGE3 = "Player successfully deleted!";
    private static final String MATCH_CODE = "ABCdef";
    private static final Integer WINNER = 1;
    private static final Integer MATCH_ID = 1;
    private static final Pageable PAGEABLE= PageRequest.of(PAGE_NUMBER, NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.asc("user.username")));
    private static final Pageable PAGEABLE_2= PageRequest.of(PAGE_NUMBER, NUMBER_OF_ELEMENTS_PER_PAGE,  Sort.by(Sort.Order.desc("startDate")));
    private static final String PAGE_URL = "?page=0";


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PlayerGooseStatsService pGooseStatsService;
    @MockBean
    private PlayerLudoStatsService pLudoStatsService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private UserService userService;
    @MockBean
    private GooseMatchService gooseMatchService;
    @MockBean
    private LudoMatchService ludoMatchService;


    private PlayerGooseStats juanGStats;
    private PlayerLudoStats juanLStats;
    private Player Juan;


    @BeforeEach
    void setup() {
    	GooseMatch gMatch = new GooseMatch();
    	gMatch.setMatchCode(MATCH_CODE);
    	gMatch.setId(MATCH_ID);
    	gMatch.setStartDate(new Date());
    	gMatch.setEndDate(null);
    	LudoMatch lMatch = new LudoMatch();
    	lMatch.setMatchCode(MATCH_CODE);
    	lMatch.setId(MATCH_ID);
    	lMatch.setStartDate(new Date());
    	lMatch.setEndDate(null);
    	Juan = new Player();
    	User userJuan = new User();
    	Authorities authJuan = new Authorities();
    	authJuan.setAuthority(AUTH);
    	authJuan.setId(ID);
    	userJuan.setEnabled(true);
    	userJuan.setPassword(PASSWORD);
    	userJuan.setUsername(USERNAME);
    	Juan.setUser(userJuan);
    	authJuan.setUser(userJuan);
    	Set<Authorities> auths = Set.of(authJuan);
    	userJuan.setAuthorities(auths);
    	Juan.setEmail(EMAIL);
    	Juan.setId(ID);
    	juanGStats = new PlayerGooseStats();
    	juanGStats.setHasWon(WINNER);
    	//juanGStats.setGooseMatch(gMatch);
    	Optional<PlayerGooseStats> oJuanGStats = Optional.of(juanGStats);
    	Set<PlayerGooseStats> setGStats = Set.of(juanGStats);
    	juanLStats = new PlayerLudoStats();
    	juanLStats.setHasWon(WINNER);
    	//juanLStats.setLudoMatch(lMatch);
    	Optional<PlayerLudoStats> oJuanLStats = Optional.of(juanLStats);
    	Set<PlayerLudoStats> setLStats = Set.of(juanLStats);
    	Optional<Player> oJuan = Optional.of(Juan);
    	List<Player> playerSet = List.of(Juan);
    	gMatch.setStats(setGStats);
    	Optional<GooseMatch> oGooseMatch = Optional.of(gMatch);
    	Set<GooseMatch> setGooseM = Set.of(gMatch);
    	lMatch.setStats(setLStats);
    	Optional<LudoMatch> oLudoMatch = Optional.of(lMatch);
    	Set<LudoMatch> setLudoM = Set.of(lMatch);
    	Slice<Player> sPlayers =new SliceImpl<Player>(playerSet);
    	List<GooseMatch> lGMatch = List.of(gMatch);
    	List<LudoMatch> lLMatch = List.of(lMatch);
    	Slice<GooseMatch> sGMatches =new SliceImpl<GooseMatch>(lGMatch);
    	Slice<LudoMatch> sLMatches = new SliceImpl<LudoMatch>(lLMatch);
    	given(this.userService.isAuthenticated()).willReturn(true);
    	given(this.playerService.findPlayerByUsername(USERNAME)).willReturn(oJuan);
    	given(this.pGooseStatsService.findPlayerGooseStatsByUsername(USERNAME)).willReturn(setGStats);
    	given(this.pLudoStatsService.findPlayerLudoStatsByUsername(USERNAME)).willReturn(setLStats);
    	given(this.pGooseStatsService.sumStats(setGStats)).willReturn(juanGStats);
    	given(this.pLudoStatsService.sumStats(setLStats)).willReturn(juanLStats);
    	given(this.playerService.findPlayerById(ID)).willReturn(oJuan);
    	given(this.playerService.findAll()).willReturn(playerSet);
    	given(this.gooseMatchService.findLobbyByUsername(USERNAME)).willReturn(oGooseMatch);
    	given(this.ludoMatchService.findLobbyByUsername(USERNAME)).willReturn(oLudoMatch);
    	given(this.playerService.findAllFilteringByUsername(USERNAME, PAGEABLE)).willReturn(sPlayers);
    	given(this.ludoMatchService.findMatchesByUsername(USERNAME)).willReturn(setLudoM);
    	given(this.gooseMatchService.findMatchesByUsername(USERNAME)).willReturn(setGooseM);
    	given(this.gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE)).willReturn(oGooseMatch);
    	given(this.ludoMatchService.findludoMatchByMatchCode(MATCH_CODE)).willReturn(oLudoMatch);
    	given(this.pGooseStatsService.findGooseStatsByUsernamedAndMatchId(USERNAME, MATCH_ID))
    	.willReturn(oJuanGStats);
    	given(this.pLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(USERNAME, MATCH_ID))
    	.willReturn(oJuanLStats);
    	given(this.playerService.findAllPaging(PAGEABLE)).willReturn(sPlayers);
    	given(this.gooseMatchService.findMatchesByUsernameWithPaging(USERNAME, PAGEABLE_2)).willReturn(sGMatches);
    	given(this.ludoMatchService.findMatchesByUsernameWithPaging(USERNAME, PAGEABLE_2)).willReturn(sLMatches);
    	given(this.pGooseStatsService.sumStats(setGStats)).willReturn(juanGStats);
    	given(this.pLudoStatsService.sumStats(setLStats)).willReturn(juanLStats);
    	given(this.playerService.findWinnerByGooseMatchCode(MATCH_CODE)).willReturn(oJuan);
    	given(this.playerService.findWinnerByLudoMatchCode(MATCH_CODE)).willReturn(oJuan);

    }

    @WithMockUser(value = USERNAME)
    @Test
    void testRedirectToProfile() throws Exception {
        mockMvc.perform(get("/players/ownProfile"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/players/"+ID));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testShowPlayer() throws Exception {
        mockMvc.perform(get("/players/"+ID))
        	.andExpect(model().attributeExists("gooseStats")) //Por no sacar los stats fuera
        	.andExpect(model().attributeExists("ludoStats"))  //como static
        	.andExpect(status().isOk());
    }


    @WithMockUser(value = USERNAME)
    @Test
    void testShowAllPlayers() throws Exception {
    	mockMvc.perform(get("/players").param("page", String.valueOf(PAGE_NUMBER)))
    	.andExpect(status().isOk())
    	.andExpect(view().name("players/listPlayers"))
    	.andExpect(model().attributeExists("playersInGame"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testFilterPlayers() throws Exception {
    	mockMvc.perform(post("/players").param("page", String.valueOf(PAGE_NUMBER)).param("Username", USERNAME))
    	.andExpect(status().isOk())
    	.andExpect(view().name("players/listPlayers"))
    	.andExpect(model().attributeExists("players"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testInitUpdatePlayerForm() throws Exception {
    	mockMvc.perform(get("/players/"+ ID +"/edit"))
    	.andExpect(status().isOk())
    	.andExpect(view().name("players/UpdatePlayerForm"))
    	.andExpect(model().attributeExists("player"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testProcessUpdatePlayerForm() throws Exception {
    	mockMvc.perform(post("/players/"+ ID +"/edit")
    			.param("user.password",NEW_PASSWORD)
    			.param("email", NEW_EMAIL))
    	.andExpect(status().is3xxRedirection())
    	.andExpect(redirectedUrl("/players/"+ID));
    }

    @WithMockUser(value = "spring")
    @Test
    void testDisablePlayer() throws Exception {
    	mockMvc.perform(get("/players/disable/"+ID))
    	.andExpect(status().is3xxRedirection())
    	.andExpect(redirectedUrl("/players?page=0"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testEnablePlayer() throws Exception {
    	mockMvc.perform(get("/players/enable/"+ID))
    	.andExpect(status().is3xxRedirection())
    	.andExpect(redirectedUrl("/players?page=0"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testDeletePlayer() throws Exception {
    	mockMvc.perform(get("/players/"+ ID +"/delete"))
    	.andExpect(status().isOk())
    	.andExpect(view().name("welcome"))
    	.andExpect(model().attribute("message", MESSAGE3));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testLudoMatchesOfPlayer() throws Exception {
    	mockMvc.perform(get("/players/"+ID+"/ludoMatchesPlayed").param("page", String.valueOf(PAGE_NUMBER)))
    	.andExpect(status().isOk())
    	.andExpect(view().name("matches/listMatchesInProfile"))
    	.andExpect(model().attributeExists("playerId"))
    	.andExpect(model().attributeExists("matches"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testGooseMatchesOfPlayer() throws Exception {
    	mockMvc.perform(get("/players/"+ID+"/gooseMatchesPlayed").param("page", String.valueOf(PAGE_NUMBER)))
    	.andExpect(status().isOk())
    	.andExpect(view().name("matches/listMatchesInProfile"))
    	.andExpect(model().attributeExists("playerId"))
    	.andExpect(model().attributeExists("matches"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testGooseMatchOfPlayer() throws Exception {
    	mockMvc.perform(get("/players/"+ID+"/matchStats/"+MATCH_CODE))
    	.andExpect(status().isOk())
    	.andExpect(view().name("stats/userStatsInAMatch"))
    	.andExpect(model().attributeExists("gooseStats"));
    }
	 @WithMockUser(value = USERNAME)
	    @Test
	    void testTotalStats() throws Exception {
	    	mockMvc.perform(get("/stats"))
	    	.andExpect(status().isOk())
	    	.andExpect(view().name("stats/totalStats"))
	    	.andExpect(model().attribute("numberOfLudoGames", 0))
	    	.andExpect(model().attribute("numberOfGooseGames", 0));
	    }

	    @WithMockUser(value = USERNAME)
	    @Test
	    void testStatsOfPlayerInMatch() throws Exception {
	    	mockMvc.perform(get("/players/"+ID+"/matchStats/"+MATCH_CODE))
	    	.andExpect(status().isOk())
	    	.andExpect(view().name("stats/userStatsInAMatch"))
	    	.andExpect(model().attribute("gooseStats", juanGStats));
	    }
	    @WithMockUser(value = USERNAME)
	    @Test
	    void testGooseMatchesPlayed()  throws Exception {
	    	mockMvc.perform(get("/players/"+ID+"/gooseMatchesPlayed").param("page", "0"))
	    	.andExpect(status().isOk())
	    	.andExpect(view().name("matches/listMatchesInProfile"))
	    	.andExpect(model().attributeExists("matches"));
	    }
	    @WithMockUser(value = USERNAME)
	    @Test
	    void testLudoMatchesPlayed()  throws Exception {
	    	mockMvc.perform(get("/players/"+ID+"/ludoMatchesPlayed").param("page", "0"))
	    	.andExpect(status().isOk())
	    	.andExpect(view().name("matches/listMatchesInProfile"))
	    	.andExpect(model().attributeExists("matches"));
	    }

	    @WithMockUser(value = "spring")
	    @Test
	    void testProcessUpdatePlayerFormNegative() throws Exception {
	    	mockMvc.perform(post("/players/"+ ID +"/edit"))
	    	.andExpect(status().is4xxClientError());
	    }

	    @WithMockUser(value = USERNAME)
	    @Test
	    void testStatsOfPlayerInMatchNotPresent() throws Exception {
	    	given(this.gooseMatchService.findGooseMatchByMatchCode(MATCH_CODE))
	    	.willReturn(Optional.empty());
	    	LudoMatch newMatch =  TestDataGenerator.generateLudoMatch(MATCH_CODE);
	    	PlayerLudoStats stats = TestDataGenerator.generatePlayerLudoStats(Juan);
	    	Set<PlayerLudoStats> statSet = Set.of(stats);
	    	newMatch.setStats(statSet);
	    	newMatch.setId(MATCH_ID);
	    	given(this.ludoMatchService.findludoMatchByMatchCode(MATCH_CODE))
	    	.willReturn(Optional.of(newMatch));
	    	given(this.pLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(USERNAME, MATCH_ID))
	    	.willReturn(Optional.of(stats));
	    	mockMvc.perform(get("/players/"+ID+"/matchStats/"+MATCH_CODE))
	    	.andExpect(status().isOk())
	    	.andExpect(view().name("stats/userStatsInAMatch"))
	    	.andExpect(model().attribute("ludoStats", stats));
	    }
}
