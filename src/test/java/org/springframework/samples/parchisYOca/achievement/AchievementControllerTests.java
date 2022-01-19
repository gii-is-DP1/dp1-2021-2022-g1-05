package org.springframework.samples.parchisYOca.achievement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.*;
import org.springframework.samples.parchisYOca.achievement.exceptions.AchievementAlreadyExists;
import org.springframework.samples.parchisYOca.achievement.exceptions.NameAlreadyExists;
import org.springframework.samples.parchisYOca.configuration.SecurityConfiguration;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AchievementController.class}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class), excludeAutoConfiguration = SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class AchievementControllerTests {

    private static final int TEST_ACHIEVEMENT_ID = 1;
    private static final String USERNAME = "ManuK";
    private static final String GAME_TO_CREATE_ACHIEVEMENT = "goose";
    private static final String PAGE_NUMBER = "0";
    private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 5;
    private static final String CORRECT_DESCRIPTION = "Number of ludo games won";
    private static final String CORRECT_NAME = "Win 30 ludo games";
    private static final String EMPTY_NAME = "";
    private static final String WRONG_NUMBER_TO_BEAT = "ashbdaimkd";
    private static final Integer CORRECT_NUMBER_TO_BEAT = 30;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AchievementController achievementController;
    @MockBean
    private AchievementService achievementService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthoritiesService authoritiesService;

    @BeforeEach
    void setup() {
        Achievement achievement1 = new Achievement();
        achievement1.setDescription("Number of eaten tokens");
        achievement1.setName("Eat 30 tokens");
        achievement1.setNumberToBeat("30");
        Achievement achievement2 = new Achievement();
        achievement2.setDescription("Number of times landed on goose squares");
        achievement2.setName("Land on 20 goose squares");
        achievement2.setNumberToBeat("20");
        Achievement achievement3 = new Achievement();
        achievement3.setDescription("Number of goose games won");
        achievement3.setName("Win 5 goose games");
        achievement3.setNumberToBeat("5");
        Player player = new Player();
        player.setAchievements(Set.of(achievement2));
        Pageable pageable = PageRequest.of(Integer.parseInt(PAGE_NUMBER), NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.asc("name")));
        List<Achievement> achievementList = List.of(achievement1,achievement2,achievement3);
        Slice<Achievement> achievementSlice = new SliceImpl<>(achievementList);

        given(this.achievementService.findAllPaging(pageable)).willReturn(achievementSlice);
        given(this.achievementService.findAll()).willReturn(achievementList);
        given(this.achievementService.findAchievementById(TEST_ACHIEVEMENT_ID)).willReturn(Optional.of(achievement1));
        given(this.playerService.findPlayerByUsername(USERNAME)).willReturn(Optional.of(player));
    }



    @WithMockUser(value = USERNAME)
    @Test
    void testListAchievements() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        mockMvc.perform(get("/achievements").param("page", PAGE_NUMBER))
            .andExpect(status().isOk())
            .andExpect(view().name("achievements/listAchievements"))
            .andExpect(model().attributeExists("isAdmin"))
            .andExpect(model().attributeExists("unlockedAchievements"))
            .andExpect(model().attributeExists("achievements"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testListAchievementsNotAuthenticated() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(false);
        mockMvc.perform(get("/achievements").param("page", PAGE_NUMBER))
            .andExpect(status().isOk())
            .andExpect(view().name("welcome"))
            .andExpect(model().attributeDoesNotExist("isAdmin"))
            .andExpect(model().attributeDoesNotExist("unlockedAchievements"))
            .andExpect(model().attributeDoesNotExist("achievements"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testDeleteAchievement() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        mockMvc.perform(get("/achievements/delete/"+ TEST_ACHIEVEMENT_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/achievements?page=0"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testPrepareForm() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(false);
        mockMvc.perform(get("/achievements/newAchievement/"+ GAME_TO_CREATE_ACHIEVEMENT))
            .andExpect(status().isOk())
            .andExpect(view().name("achievements/createAchievement"))
            .andExpect(model().attributeExists("descriptions"))
            .andExpect(model().attributeExists("achievement"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testCreateAchievement() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        mockMvc.perform(post("/achievements/newAchievement/" + GAME_TO_CREATE_ACHIEVEMENT)
                .param("name", CORRECT_NAME)
                .param("description", CORRECT_DESCRIPTION)
                .param("numberToBeat", String.valueOf(CORRECT_NUMBER_TO_BEAT)))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/achievements?page=0"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testCreateAchievementEmptyName() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        mockMvc.perform(post("/achievements/newAchievement/" + GAME_TO_CREATE_ACHIEVEMENT)
                .param("name", EMPTY_NAME)
                .param("description", CORRECT_DESCRIPTION)
                .param("numberToBeat", String.valueOf(CORRECT_NUMBER_TO_BEAT)))
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("achievement"))
            .andExpect(model().attributeExists("descriptions"))
            .andExpect(model().attributeExists("achievement"))
            .andExpect(view().name("achievements/createAchievement"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testCreateAchievementWrongName() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        given(this.achievementService.save(any(Achievement.class))).willThrow(NumberFormatException.class);

        mockMvc.perform(post("/achievements/newAchievement/" + GAME_TO_CREATE_ACHIEVEMENT)
                .param("name", CORRECT_NAME)
                .param("description", CORRECT_DESCRIPTION)
                .param("numberToBeat", WRONG_NUMBER_TO_BEAT))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message", "The number to beat must be a number"))
            .andExpect(model().attributeExists("achievement"))
            .andExpect(view().name("achievements/createAchievement"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testCreateExistingAchievement() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        given(this.achievementService.save(any(Achievement.class))).willThrow(AchievementAlreadyExists.class);

        mockMvc.perform(post("/achievements/newAchievement/" + GAME_TO_CREATE_ACHIEVEMENT)
                .param("name", CORRECT_NAME)
                .param("description", CORRECT_DESCRIPTION)
                .param("numberToBeat", String.valueOf(CORRECT_NUMBER_TO_BEAT)))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message", "The achievement has the same description and number as another"))
            .andExpect(model().attributeExists("achievement"))
            .andExpect(view().name("achievements/createAchievement"));
    }

    @WithMockUser(value = USERNAME)
    @Test
    void testCreateExistingAchievementName() throws Exception {
        given(this.userService.isAuthenticated()).willReturn(true);
        given(this.achievementService.save(any(Achievement.class))).willThrow(NameAlreadyExists.class);

        mockMvc.perform(post("/achievements/newAchievement/" + GAME_TO_CREATE_ACHIEVEMENT)
                .param("name", CORRECT_NAME)
                .param("description", CORRECT_DESCRIPTION)
                .param("numberToBeat", String.valueOf(CORRECT_NUMBER_TO_BEAT)))
            .andExpect(status().isOk())
            .andExpect(model().attribute("message", "The name of the achievement already exists"))
            .andExpect(model().attributeExists("achievement"))
            .andExpect(view().name("achievements/createAchievement"));
    }
}
