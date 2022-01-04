package org.springframework.samples.parchisYOca.user;

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
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
	private static final String PASSWORD = "username";
	private static final String EMAIL = "username@domain.com";
	
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
		given(this.playerService.findPlayerByUsername(USERNAME)).willReturn(voidOp);
		given(this.playerService.findPlayerByEmail(EMAIL)).willReturn(voidOp);
	}
	@WithMockUser(value = "spring")
	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/users/new"))
		.andExpect(status().isOk())
		.andExpect(view().name("users/createPlayerForm"));
	}
	@Test
	void testProcessCreationForm() throws Exception {
		//session.setAttribute("player", );
		mockMvc.perform(post("/users/new")
				.param("user.password", PASSWORD)
				.param("user.username", USERNAME)
				.param("email", EMAIL))
		.andExpect(status().isOk());
	}
}
