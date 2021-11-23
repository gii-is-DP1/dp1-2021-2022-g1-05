package org.springframework.samples.parchisYOca.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.user.*;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {PlayerController.class, UserController.class}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class PlayerControllerTests {

    private static final int TEST_PLAYER_ID = 1;
    private Player ManuK;

    @Autowired
    private PlayerController playerController;
    @MockBean
    private PlayerService gameService;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthoritiesService authoritiesService;
    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setup() {

        ManuK = new Player();
        User user = new User();
        user.setUsername("ManuK");
        user.setPassword("1234567");
        ManuK.setUser(user);
        ManuK.setId(TEST_PLAYER_ID);
        ManuK.setEmail("manu@gmail.com");
        given(this.gameService.findPlayerById(TEST_PLAYER_ID)).willReturn(ManuK);

    }

    @WithMockUser(value = "spring")
    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/users/new")).andExpect(status().isOk()).andExpect(status().isOk()).andExpect(model().attributeExists("player"))
            .andExpect(view().name("users/createPlayerForm"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/users/new").param("email", "manu@gmail.com").param("password", "1234567")
                .param("username", "ManuK").with(csrf()))
            .andExpect(status().is3xxRedirection());
    }
}
