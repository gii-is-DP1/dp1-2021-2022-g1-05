package org.springframework.samples.parchisYOca.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = UserController.class,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
classes = WebSecurityConfigurer.class),
excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
	private static final String USERNAME = "username";
	private static final String TAKEN_USERNAME = "username2";
	private static final String EMPTY_USERNAME = "";
	private static final String PASSWORD = "username7";
	private static final String BAD_PASSWORD = "username";
	private static final String EMAIL = "username@domain.com";
	private static final String TAKEN_EMAIL = "username2@domain.com";
	private static final String UN_ERROR_MSG = "That username is already taken";
	private static final String EUN_ERROR_MSG = "The username can't be empty";
	private static final String MAIL_ERROR_MSG = "That email is already taken";
	private static final String PSSW_ERROR_MSG ="The password must be at least 7 characters long and contain a number";

	@Autowired
    private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext context;
	@MockBean
	private UserService userService;
	@MockBean
	private PlayerService playerService;

	@BeforeEach
	void setup() {
		Optional<Player> voidOp = Optional.empty();
		Player player = new Player();
		Optional<Player> takenPlayer = Optional.of(player);
		given(this.playerService.findPlayerByUsername(USERNAME)).willReturn(voidOp);
		given(this.playerService.findPlayerByUsername(TAKEN_USERNAME)).willReturn(takenPlayer);
		given(this.playerService.findPlayerByEmail(EMAIL)).willReturn(voidOp);
		given(this.playerService.findPlayerByEmail(TAKEN_EMAIL)).willReturn(takenPlayer);
	}
	@WithMockUser(value = "spring")
	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/users/new"))
		.andExpect(status().isOk())
		.andExpect(model().attributeExists("player"))
		.andExpect(view().name("users/createPlayerForm"));
	}
	@Test
	void testProcessCreationForm() throws Exception {
		//session.setAttribute("player", );
		mockMvc.perform(post("/users/new")
				.param("user.password", PASSWORD)
				.param("user.username", USERNAME)
				.param("email", EMAIL))
		.andExpect(status().is3xxRedirection());
	}
	@Test
	void testProcessCreationFormPasswordException() throws Exception {
		//session.setAttribute("player", );
		mockMvc.perform(post("/users/new")
				.param("user.password", BAD_PASSWORD)
				.param("user.username", USERNAME)
				.param("email", EMAIL))
		.andExpect(status().isOk())
		.andExpect(model().attribute("message", PSSW_ERROR_MSG));
	}
	@Test
	void testProcessCreationFormEmptyUserNmaeException() throws Exception {
		//session.setAttribute("player", );
		mockMvc.perform(post("/users/new")
				.param("user.password", PASSWORD)
				.param("user.username", EMPTY_USERNAME)
				.param("email", EMAIL))
		.andExpect(status().isOk())
		.andExpect(model().attribute("message", EUN_ERROR_MSG));
	}
	@Test
	void testProcessCreationFormUserNameException() throws Exception {
		//session.setAttribute("player", );
		mockMvc.perform(post("/users/new")
				.param("user.password", PASSWORD)
				.param("user.username", TAKEN_USERNAME)
				.param("email", EMAIL))
		.andExpect(status().isOk())
		.andExpect(model().attribute("message", UN_ERROR_MSG));
	}
	@Test
	void testProcessCreationFormEmailException() throws Exception {
		//session.setAttribute("player", );
		mockMvc.perform(post("/users/new")
				.param("user.password", PASSWORD)
				.param("user.username", USERNAME)
				.param("email", TAKEN_EMAIL))
		.andExpect(status().isOk())
		.andExpect(model().attribute("message", MAIL_ERROR_MSG));
	}
}
