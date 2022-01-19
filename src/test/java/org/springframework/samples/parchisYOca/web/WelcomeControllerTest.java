package org.springframework.samples.parchisYOca.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WelcomeController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = WebSecurityConfigurer.class),
    excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class WelcomeControllerTest {

    protected final String USERNAME = "ManuK";
    protected final String PASSWORD = "1234567";
    protected final List<GrantedAuthority> AUTHORITIES = List.of();
    protected final Integer PLAYER_ID = 1;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WelcomeController welcomeController;
    @MockBean
    private UserService userService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private GooseMatchService gooseMatchService;
    @MockBean
    private LudoMatchService ludoMatchService;

    @BeforeEach
    public void setup(){
        Player player = new Player();
        player.setId(PLAYER_ID);
        User userDetails = new User(USERNAME, PASSWORD, AUTHORITIES);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(userDetails);
        given(this.userService.isAuthenticated()).willReturn(true);
        given(this.playerService.findPlayerByUsername(USERNAME)).willReturn(Optional.of(player));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testWelcomeWithoutRunningGamesNeitherOwnerLeft() throws Exception {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/")
            .session(session);

        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerId"))
            .andExpect(view().name("welcome"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testWelcomeWithoutRunningGamesWithOwnerLeft() throws Exception {
        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/")
            .session(session);
        session.setAttribute("ownerLeft", "The owner of the lobby left, so it was closed");

        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerId"))
            .andExpect(model().attribute("message", "The owner of the lobby left, so it was closed"))
            .andExpect(view().name("welcome"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testWelcomeWithGooseGameRunnnig() throws Exception {
        GooseMatch gm = new GooseMatch();
        given(gooseMatchService.findLobbyByUsername(USERNAME)).willReturn(Optional.of(gm));


        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/")
            .session(session);

        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerId"))
            .andExpect(model().attribute("inGooseMatch", 1))
            .andExpect(view().name("welcome"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testWelcomeWithLudoGameRunnnig() throws Exception {
        LudoMatch lm = new LudoMatch();
        given(ludoMatchService.findLobbyByUsername(USERNAME)).willReturn(Optional.of(lm));

        MockHttpSession session = new MockHttpSession();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/")
            .session(session);

        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("playerId"))
            .andExpect(model().attribute("inLudoMatch", 1))
            .andExpect(view().name("welcome"));
    }
}
