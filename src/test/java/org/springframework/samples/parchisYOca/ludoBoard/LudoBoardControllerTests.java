package org.springframework.samples.parchisYOca.ludoBoard;

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
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardController;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoChip.GameState;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.Authorities;
import org.springframework.samples.parchisYOca.user.User;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LudoBoardController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = WebSecurityConfigurer.class),
    excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class LudoBoardControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private UserService userService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerLudoStatsService playerLudoStatsService;
    @MockBean
    private LudoChipService ludoChipService;
    @MockBean
    private LudoMatchService ludoMatchService;
    @MockBean
    private LudoBoardService ludoBoardService;


    public static final Integer INDICE_PRIMER_DADO = 0;
    public static final Integer INDICE_SEGUNDO_DADO = 1;
    public static final Integer INDICE_SUMA_DADOS = 2;
    public static final Integer PRIMER_DADO_5=0;
    public static final Integer SEGUNDO_DADO_5=1;
    public static final Integer SUMA_DADOS_5=2;
    public static final Integer DOS_DADOS_5=3;
    private static final String ALL_CHIPS_OF_A_PLAYER ="0123";

    public static final Integer ATE_CHIP=1;
    public static final Integer LANDED_FINAL=2;
    public static final Integer GOT_BLOCKED=3;
    public static final Integer BLOCKED_AND_ATE=4;
    public static final Integer ENDED_THE_GAME=5;


    //Constantes de test
    private static final int NOTURN = 0;
    private static final int ID = 1;
    private static final String AUTH = "admin";
    private static final Boolean LOGGED_IN =true;
    private static final Boolean NOT_LOGGED=false;
    private static final Boolean TRUE =true;
    private static final Integer JAIME_ID = 5;
    private static final Integer JAIME_INGAMEID = 0;
    private PlayerLudoStats jaimeStats = new PlayerLudoStats();

    List<LudoChip> chips=new ArrayList<>();
    private LudoChip chip1=new LudoChip();
    private LudoChip chip2=new LudoChip();
    private LudoChip chip3=new LudoChip();
    private LudoChip chip4=new LudoChip();
    private LudoChip chip5=new LudoChip();
    private LudoChip chip6=new LudoChip();
    private LudoChip chip7=new LudoChip();
    private LudoChip chip8=new LudoChip();
    List<LudoChip> blockChips=new ArrayList<>();


    private static final Integer PACO_ID = 6;
    private static final Integer PACO_INGAMEID = 1;
    private static final Integer IS_OWNER =1;
    private Player Jaime;
    private Player Paco;
    private static final String JAIME = "Jaime";
    private static final String PACO = "Paco";
    private PlayerLudoStats pacoStats;

    private static final Integer MATCH_ID = 1;
    private static final Integer HASTURN=1;
    private static final int[] DICES = {3,2,5};
    private static final int[] DICES_NO_5={1,2,3};
    private static final int[] DICES_0={-1,0,-1};

    private static final String MATCH_CODE = "abcdfg";
    private static final int[] DOUBLE_DICES = {3,3,6};
    private static final boolean DOUBLES=true;
    private static final boolean NO_DOUBLES = false;

    private static LudoMatch match;






    @BeforeEach
    void setup(){
        Jaime=new Player();
        User userJaime = new User();
        userJaime.setUsername(JAIME);
        userJaime.setPassword(JAIME);
        Optional<User> oUserJaime = Optional.of(userJaime);
        Jaime.setUser(userJaime);
        Jaime.setEmail("jaime@domain.com");
        Jaime.setId(JAIME_ID);
        jaimeStats = new PlayerLudoStats();
        jaimeStats.setPlayer(Jaime);
        jaimeStats.setHasTurn(HASTURN);
        Authorities authJaime = new Authorities();
        authJaime.setAuthority(AUTH);
        authJaime.setId(ID);
        chip1.setInGamePlayerId(JAIME_INGAMEID);
        chip2.setInGamePlayerId(JAIME_INGAMEID);
        chip3.setInGamePlayerId(JAIME_INGAMEID);
        chip4.setInGamePlayerId(JAIME_INGAMEID);
        chip1.setColor();
        chip2.setColor();
        chip3.setColor();
        chip4.setColor();
        chip3.setGameState(GameState.midGame);
        chip4.setGameState(GameState.midGame);
        chip3.setPosition(3);
        chip4.setPosition(3);
        blockChips.add(chip3);
        blockChips.add(chip4);

        Paco = new Player();
        User userPaco = new User();
        userPaco.setUsername(PACO);
        userPaco.setPassword(PACO);
        Optional<User> oUserPaco = Optional.of(userPaco);
        Paco.setUser(userPaco);
        Paco.setEmail("Paco@domain.com");
        Paco.setId(PACO_ID);
        pacoStats = new PlayerLudoStats();
        pacoStats.setPlayer(Paco);
        pacoStats.setIsOwner(IS_OWNER);
        pacoStats.setHasTurn(-1);

        chip5.setInGamePlayerId(PACO_INGAMEID);
        chip6.setInGamePlayerId(PACO_INGAMEID);
        chip7.setInGamePlayerId(PACO_INGAMEID);
        chip8.setInGamePlayerId(PACO_INGAMEID);
        chip5.setColor();
        chip6.setColor();

        chips.add(chip1);
        chips.add(chip2);
        chips.add(chip3);
        chips.add(chip4);
        chips.add(chip5);
        chips.add(chip6);
        chips.add(chip7);
        chips.add(chip8);

        Set<PlayerLudoStats> playerStats= new HashSet<>();
        playerStats.add(jaimeStats);
        playerStats.add(pacoStats);


        Optional<PlayerLudoStats> oJaimeStats = Optional.of(jaimeStats);
        Optional<PlayerLudoStats> oPacoStats = Optional.of(pacoStats);

        match = new LudoMatch();
        match.setMatchCode(MATCH_CODE);
        match.setId(MATCH_ID);
        match.setStats(playerStats);

        LudoBoard board=new LudoBoard();
        Set<LudoChip> auxChipSet=new HashSet<>(chips);
        board.setChips(auxChipSet);
        match.setBoard(board);

        Optional<LudoMatch> oMatch = Optional.of(match);

        given(ludoChipService.findChipsByMatchId(MATCH_ID)).willReturn(chips);
        given(ludoBoardService.checkGreedy(jaimeStats,NO_DOUBLES)).willReturn(false);
        given(this.playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(
            JAIME, MATCH_ID)).willReturn(oJaimeStats);
        given(ludoMatchService.findludoMatchById(MATCH_ID)).willReturn(oMatch);
        given(playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(PACO_INGAMEID, MATCH_ID)).willReturn(oPacoStats);
        given(ludoChipService.noChipsOutOfHome(chips,JAIME_INGAMEID)).willReturn(false);
    }



    @WithMockUser(value = JAIME)
    @Test
    void testLudoDicesRolledNotLogged() throws Exception {
        MockHttpSession session = new MockHttpSession();

        given(this.userService.isAuthenticated()).willReturn(NOT_LOGGED);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/"));
    }
    @WithMockUser(value = JAIME)
    @Test
    void testLudoDicesRolledGreedyRoll() throws Exception {
        MockHttpSession session = new MockHttpSession();

        session.setAttribute("dices", DOUBLE_DICES);
        session.setAttribute("fromLudo",TRUE);
        session.setAttribute("matchId", MATCH_ID);

        given(ludoBoardService.checkGreedy(jaimeStats,DOUBLES)).willReturn(TRUE);
        given(this.userService.isAuthenticated()).willReturn(LOGGED_IN);


        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ludoMatches/" + MATCH_ID));
        assertThat(session.getAttribute("especial")).isEqualTo("You managed to roll doubles THREE TIMES? Preposterous, go back home.");
    }
    @WithMockUser(value = JAIME)
    @Test
    void testLudoDicesRolled5() throws Exception {
        MockHttpSession session = new MockHttpSession();

        session.setAttribute("dices", DICES);
        session.setAttribute("fromLudo",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        given(this.userService.isAuthenticated()).willReturn(LOGGED_IN);
        given( ludoChipService.manageFives(JAIME_INGAMEID,MATCH_ID,
            DICES[INDICE_PRIMER_DADO], DICES[INDICE_SEGUNDO_DADO])).willReturn(SUMA_DADOS_5);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ludoMatches/" + MATCH_ID));
        assertThat(session.getAttribute("especial")).isEqualTo("Your roll sums 5, so one of your chips got taken out");
    }

    @WithMockUser(value = JAIME)
    @Test
    void testLudoDicesRolledNoChipsOutOfHome() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DICES_NO_5);
        session.setAttribute("fromLudo",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        given(this.userService.isAuthenticated()).willReturn(LOGGED_IN);
        given(ludoChipService.noChipsOutOfHome(chips,JAIME_INGAMEID)).willReturn(TRUE);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ludoMatches/" + MATCH_ID));
        assertThat(session.getAttribute("especial")).isEqualTo("You weren't able to take out any of your chips :(");


        session.setAttribute("dices", DOUBLE_DICES);
        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder2)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ludoMatches/" + MATCH_ID));
        assertThat(session.getAttribute("especial")).isEqualTo("You couldn't take out any chips, but you rolled double, sou you get an extra turn!");

    }

    @WithMockUser(value = JAIME)
    @Test
    void testLudoDicesRolledDoubles() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("dices", DOUBLE_DICES);
        session.setAttribute("fromLudo",TRUE);
        session.setAttribute("matchId", MATCH_ID);

        given(this.userService.isAuthenticated()).willReturn(LOGGED_IN);
        given(ludoChipService.breakBlocks(chips, JAIME_INGAMEID)).willReturn(blockChips);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ludoInGame/chooseChip/0"));
        assertThat(session.getAttribute("especial")).isNull();

        given(ludoChipService.breakBlocks(chips, JAIME_INGAMEID)).willReturn(new ArrayList<>());
        MockHttpServletRequestBuilder builder2 = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder2)
            .andExpect(status().is2xxSuccessful())
            .andExpect(view().name("matches/ludoMatch"));
        assertThat(session.getAttribute("especial")).isEqualTo("You got a double roll!! You can roll the dice again.");
    }

    @WithMockUser(value = JAIME)
    @Test
    void testLudoDicesRolledNoMoreMoves() throws Exception {
        MockHttpSession session = new MockHttpSession();

        session.setAttribute("dices", DICES_0);
        session.setAttribute("fromLudo",TRUE);
        session.setAttribute("matchId", MATCH_ID);
        given(this.userService.isAuthenticated()).willReturn(LOGGED_IN);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/ludoInGame/dicesRolled")
            .session(session);
        mockMvc.perform(builder)
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/ludoMatches/" + MATCH_ID));

    }









}
