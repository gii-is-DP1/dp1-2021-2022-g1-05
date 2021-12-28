package org.springframework.samples.parchisYOca.achievement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AchievementController.class}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
public class AchievementControllerTests {

    private static final int TEST_ACHIEVEMENT_ID = 1;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AchievementController achievementController;
    @MockBean
    private AchievementService achievementService;
    @MockBean
    private PlayerService gameService;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthoritiesService authoritiesService;

    @WithMockUser(value = "spring")
    @Test
    void testListAchievements() throws Exception {
        mockMvc.perform(get("/achievements"))
            .andExpect(status().isOk())
            .andExpect(view().name("achievements/listAchievements"))
            .andExpect(model().attributeExists("achievements"));

    }

    @WithMockUser(value = "spring")
    @Test
    void testDeleteAchievement() throws Exception {
        mockMvc.perform(get("/achievements/delete/{achievementId}", TEST_ACHIEVEMENT_ID))
            .andExpect(status().isOk()).andExpect(view().name("achievements/listAchievements"))
            .andExpect(model().attributeExists("achievements"));

        assertThat(achievementService.findAchievementById(TEST_ACHIEVEMENT_ID).isEmpty());

    }
}
