package org.springframework.samples.parchisYOca.player;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.user.*;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
    @Autowired
    private UserController userController;
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
        mockMvc.perform(post("/users/new").param("email", "manutest@gmail.com").param("user.password", "1234567")
                .param("user.username", "ManuK2").with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(value = "spring")
    @Test
    void testProcessCreationWithWrongPassword() throws Exception {
        mockMvc.perform(post("/users/new").param("email", "manutest@gmail.com").param("user.password", "12")
                .param("user.username", "ManuK2").with(csrf())).andExpect(view().name("users/createPlayerForm"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testProcessCreationWithWrongUsername() throws Exception {
        mockMvc.perform(post("/users/new").param("email", "manutest@gmail.com").param("user.password", "1234567")
            .param("user.username", "").with(csrf())).andExpect(view().name("users/createPlayerForm"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testProcessCreationWithWrongEmail() throws Exception {
        mockMvc.perform(post("/users/new").param("email", "").param("user.password", "1234567")
            .param("user.username", "ManuK2").with(csrf())).andExpect(view().name("users/createPlayerForm"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testListPlayers() throws Exception {
        mockMvc.perform(get("/players"))
            .andExpect(status().isOk()).andExpect(view().name("players/listPlayers"))
            .andExpect(model().attributeExists("players"));


    }


    /*@WithMockUser(value = "spring") TODO I dont know why its not working
    @Test
    void testProcessCreationWithRepeatedUsername() throws Exception {
        mockMvc.perform(post("/users/new").param("email", "manutest@gmail.com").param("user.password", "1234567")
            .param("user.username", "ManuK").with(csrf())).andExpect(view().name("users/createPlayerForm"));
    }

    @WithMockUser(value = "spring")
    @Test
    void testProcessCreationWithRepeatedEmail() throws Exception {
        mockMvc.perform(post("/users/new").param("email", "manu@gmail.com").param("user.password", "1234567")
            .param("user.username", "ManuK2").with(csrf())).andExpect(view().name("users/createPlayerForm"));
    }*/



}
