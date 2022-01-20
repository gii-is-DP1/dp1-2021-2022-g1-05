package org.springframework.samples.parchisYOca.gooseBoard;

import org.hibernate.envers.internal.tools.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.user.Authorities;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.user.UserController;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = GooseBoardController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
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

    private static final Integer JAIL=-2;
    private static final int NOTURN = 0;
    private static final int ID = 1;
    private static final String AUTH = "admin";
	private static final Boolean LOGGED_IN =true;
    private static final Boolean NOT_LOGGED=false;
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
    private static final Integer HASTURN=1;
	private static final int[] DICES = {1,2,3};
    private static final int[] DOUBLE_DICES = {3,3,6};
    private static final boolean DOUBLES=true;
	private Player Jaime;
	private Player Paco;
	private Player Laura;
	private Player Carmen;
	private GooseMatch match;
    private List<GooseChip> chips;
    private static GooseChip chip1 = new GooseChip();
    private static GooseChip chip2 = new GooseChip();
    private static GooseChip chip3 = new GooseChip();
    private static GooseChip chip4 = new GooseChip();
    public static final int INDICE_SUMA_DADOS = 2;

    private static final String DEATH_MESSAGE = "Death";
    private static final String NORMAL_MOVE_MESSAGE = "NoDobles";
    private static final String GOOSE_MESSAGE = "Goose";
    private static final String MAZE_MESSAGE = "Maze";
    private static final String JAIL_MESSAGE = "Jail";
    private static final String DOUBLE_ROLL_MESSAGE = "Double roll";

    private static final Integer RANDOM_POSITION = 3;
    private static final Integer RANDOM_GOOSE = 14 ;
    private static final Integer JAIL_POSITION = 56 ;
    private static final Integer END_MAZE = 30 ;
    private static final Integer START_POSITION= 0 ;


    private static final boolean NO_DOUBLES = false;

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
        jaimeStats.setHasTurn(HASTURN);
        Authorities authJaime = new Authorities();
        authJaime.setAuthority(AUTH);
        authJaime.setId(ID);

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
        pacoStats.setHasTurn(-1);
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
        lauraStats.setHasTurn(-1);
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
        carmenStats.setHasTurn(-1);

        chips=new ArrayList<>();
        chip1.setInGameId(JAIME_INGAMEID);
        chip1.setPosition(0);
        chip2.setInGameId(PACO_INGAMEID);
        chip3.setInGameId(LAURA_INGAMEID);
        chip4.setInGameId(CARMEN_INGAMEID);
        chips.add(chip1);
        chips.add(chip2);
        chips.add(chip3);
        chips.add(chip4);

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
        Optional<List<GooseChip>> oChips=Optional.of(chips);

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
        given(this.gooseChipService.findChipsByMatchId(MATCH_ID))
        .willReturn(oChips.get());


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
        int[] rolledDices = (int[])session.getAttribute("dices");
        boolean flagDobles=NO_DOUBLES;
        GooseChip auxChip1 =new GooseChip();
        auxChip1.setInGameId(JAIME_INGAMEID);
        auxChip1.setPosition(0);

        given(gooseChipService.checkSpecials(JAIME,
            chip1, rolledDices[INDICE_SUMA_DADOS], flagDobles)).willReturn(new Triple<Integer,Integer,String>(RANDOM_POSITION,HASTURN,NORMAL_MOVE_MESSAGE));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));
        assertThat(session.getAttribute("especial")).isEqualTo("You moved from the square "+ auxChip1.getPosition()+ " to the square " + RANDOM_POSITION);
    }

	@WithMockUser(value = JAIME)
	@Test
	void testGooseDicesRolledGoose() throws Exception {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("dices", DICES);
		session.setAttribute("fromGoose",TRUE);
		session.setAttribute("matchId", MATCH_ID);
        int[] rolledDices = (int[])session.getAttribute("dices");
        boolean flagDobles=NO_DOUBLES;
        GooseChip auxChip1=new GooseChip();
        auxChip1.setInGameId(JAIME_INGAMEID);
        auxChip1.setPosition(6);
        chip1.setPosition(6);

        given(gooseChipService.checkSpecials(JAIME,
            chip1, rolledDices[INDICE_SUMA_DADOS], flagDobles)).willReturn(new Triple<Integer,Integer,String>(RANDOM_GOOSE,HASTURN,GOOSE_MESSAGE));

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
                .session(session);
		mockMvc.perform(builder)
		.andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));

        assertThat(session.getAttribute("especial")).isEqualTo("You have landed on the special square " + GOOSE_MESSAGE.toLowerCase(Locale.ROOT)+ ", \n"
            +"you have been moved from square " + String.valueOf(auxChip1.getPosition()+rolledDices[INDICE_SUMA_DADOS]) + " to the square "+RANDOM_GOOSE
            +". You have an extra turn!");
	}
    @WithMockUser(value = JAIME)
    @Test
    void testGooseDicesRolledDiceJail() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DICES);
        session.setAttribute("fromGoose",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        int[] rolledDices = (int[])session.getAttribute("dices");
        boolean flagDobles=NO_DOUBLES;
        given(gooseChipService.checkSpecials(JAIME,
            chip1, rolledDices[INDICE_SUMA_DADOS], flagDobles)).willReturn(new Triple<Integer,Integer,String>(JAIL_POSITION,JAIL,JAIL_MESSAGE));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));

        assertThat(session.getAttribute("especial")).isEqualTo("You have landed on the special square " + JAIL_MESSAGE.toLowerCase(Locale.ROOT)+ ", \n"
            +"you loose " + Math.abs(JAIL) + " turns :(");

    }

    @WithMockUser(value = JAIME)
    @Test
    void testGooseDicesRolledDiceMaze() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DICES);
        session.setAttribute("fromGoose",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        int[] rolledDices = (int[])session.getAttribute("dices");
        boolean flagDobles=NO_DOUBLES;
        chip1.setPosition(39);
        given(gooseChipService.checkSpecials(JAIME,
            chip1, rolledDices[INDICE_SUMA_DADOS], flagDobles)).willReturn(new Triple<Integer,Integer,String>(END_MAZE,NOTURN ,MAZE_MESSAGE));
        pacoStats.setHasTurn(0);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));

        assertThat(session.getAttribute("especial")).isEqualTo("You have landed on the special square " + MAZE_MESSAGE.toLowerCase(Locale.ROOT)+ ", \n"
            + "you have been moved to the square "+END_MAZE+ ". Today it's not your lucky day ¯\\('-')_/¯");
    }
    @WithMockUser(value = JAIME)
    @Test
    void testGooseDicesRolledDoubles() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DOUBLE_DICES);
        session.setAttribute("fromGoose",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        int[] rolledDices = (int[])session.getAttribute("dices");
        boolean flagDobles=DOUBLES;
        given(gooseChipService.checkSpecials(JAIME,
            chip1, rolledDices[INDICE_SUMA_DADOS], flagDobles)).willReturn(new Triple<Integer,Integer,String>(RANDOM_POSITION,HASTURN,DOUBLE_ROLL_MESSAGE));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));

        assertThat(session.getAttribute("especial")).isEqualTo("You have landed on the square " +RANDOM_POSITION +" and you got a double roll!! You can roll the dice again");
    }
    @WithMockUser(value = JAIME)
    @Test
    void testGooseDicesRolledDeath() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DICES);
        session.setAttribute("fromGoose",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        int[] rolledDices = (int[])session.getAttribute("dices");
        boolean flagDobles=NO_DOUBLES;
        given(gooseChipService.checkSpecials(JAIME,
            chip1, rolledDices[INDICE_SUMA_DADOS], flagDobles)).willReturn(new Triple<Integer,Integer,String>(START_POSITION,NOTURN,DEATH_MESSAGE));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));
        assertThat(session.getAttribute("especial")).isEqualTo("You have landed on the special square " + DEATH_MESSAGE.toLowerCase(Locale.ROOT)+ ", \n"
            + "you have been moved to the square "+START_POSITION+ ". Today it's not your lucky day ¯\\('-')_/¯");

    }
    @WithMockUser(value = PACO)
    @Test
    void testGooseDicesRolledNoTurn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DICES);
        session.setAttribute("fromGoose",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        int[] rolledDices = (int[])session.getAttribute("dices");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/gooseMatches/"+MATCH_ID));
    }
    @WithMockUser(value = JAIME)
    @Test
    void testGooseDicesRolledDiceNotAuthenticated() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DICES);
        session.setAttribute("fromGoose",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        given(this.userService.isAuthenticated()).willReturn(NOT_LOGGED);
                MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/gooseInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }

}
