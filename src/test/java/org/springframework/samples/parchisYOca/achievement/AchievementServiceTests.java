package org.springframework.samples.parchisYOca.achievement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.achievement.exceptions.AchievementAlreadyExists;
import org.springframework.samples.parchisYOca.achievement.exceptions.NameAlreadyExists;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class AchievementServiceTests {

    private static final Integer TEST_ACHIEVEMENT_ID = 1;
    private static final Integer TEST_PLAYER_ID = 1;
    private static final String LUDO_DESCRIPTION = "Number of eaten tokens";
    private static final String GOOSE_DESCRIPTION = "Number of times landed on goose squares";
    private static final String NUMBER_TO_BEAT = "30";
    private static final String NAME = "Nuevo logro";
    private static final String EXISTING_NAME = "Walk 20 times on goose square";
    private static final String EXISTING_DESCRIPTION = "Number of times landed on goose squares";
    private static final String EXISTING_NUMBER_TO_BEAT = "20";
    private static final Integer NUMBER_OF_GOOSE_ACHIEVEMENTS = 4;
    private static final Integer NUMBER_OF_LUDO_ACHIEVEMENTS = 3;

    @Autowired
    private AchievementService achievementService;
    @Autowired
    private PlayerService playerService;

    @Test
    @Transactional
    public void testAddCorrectAchievement() throws AchievementAlreadyExists, NameAlreadyExists {
        Achievement newAchievement = new Achievement();
        newAchievement.setName(NAME);
        newAchievement.setDescription(LUDO_DESCRIPTION);
        newAchievement.setNumberToBeat(NUMBER_TO_BEAT);
        Achievement addedAchievement = achievementService.save(newAchievement);

        assertThat(addedAchievement).isEqualTo(achievementService.findAchievementById(addedAchievement.getId()).get());
    }


    @Test
    @Transactional
    public void testAddWithNoName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setDescription(GOOSE_DESCRIPTION);
        newAchievement.setNumberToBeat(NUMBER_TO_BEAT);

        assertThrows(ConstraintViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }


    @Test
    @Transactional
    public void testAddWithNoDescription(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName(NAME);
        newAchievement.setNumberToBeat(NUMBER_TO_BEAT);

        assertThrows(ConstraintViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }


    @Test
    @Transactional
    public void testAddWithDuplicatedName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName(EXISTING_NAME);
        newAchievement.setDescription(LUDO_DESCRIPTION);
        newAchievement.setNumberToBeat(NUMBER_TO_BEAT);

        assertThrows(NameAlreadyExists.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    @Transactional
    public void testAddAlreadyExisting(){ //Same description and number to beat
        Achievement newAchievement = new Achievement();
        newAchievement.setName(NAME);
        newAchievement.setDescription(EXISTING_DESCRIPTION);
        newAchievement.setNumberToBeat(EXISTING_NUMBER_TO_BEAT);

        assertThrows(AchievementAlreadyExists.class, () ->{
            achievementService.save(newAchievement);
        });
    }


    @Test
    @Transactional
    public void testAddNullAchievement(){
        assertThrows(NullPointerException.class, () ->{
            achievementService.save(null);
        });
    }

    @Test
    @Transactional
    public void testDeleteExistingAchievement(){
        Optional<Achievement> achievementToDelete = achievementService.findAchievementById(TEST_ACHIEVEMENT_ID);
        if(achievementToDelete.equals(Optional.empty())) {
            fail();
        } else {
            achievementService.delete(achievementToDelete.get());
            assertThat(achievementService.findAchievementById(TEST_ACHIEVEMENT_ID).isEmpty());
        }
    }

    @Test
    @Transactional
    public void testDeleteExistingAchievementAssignedToPlayer(){
        Optional<Achievement> achievementToDelete = achievementService.findAchievementById(TEST_ACHIEVEMENT_ID);
        Player playerToAssign = playerService.findPlayerById(TEST_PLAYER_ID).get();

        if(achievementToDelete.equals(Optional.empty())) {
            fail();
        } else {
            Set<Achievement> achievementsOfPlayer = playerToAssign.getAchievements();
            achievementsOfPlayer.add(achievementToDelete.get());
            playerToAssign.setAchievements(achievementsOfPlayer);
            playerService.savePlayer(playerToAssign);
            achievementService.delete(achievementToDelete.get());
            assertThat(achievementService.findAchievementById(TEST_ACHIEVEMENT_ID).isEmpty());
        }
    }

    @Test
    @Transactional
    public void testDeleteNullAchievement(){

        assertThrows(NullPointerException.class, () -> {
           achievementService.delete(null);
        });
    }

    @Test
    @Transactional
    public void testAssignGooseAchievementsToPlayer(){

        Player player = playerService.findPlayerById(TEST_PLAYER_ID).get();
        PlayerGooseStats pgs = new PlayerGooseStats();
        pgs.setPlayer(player);
        pgs.setHasWon(40);
        pgs.setLandedDice(400);
        pgs.setLandedGeese(120);
        achievementService.checkGooseAchievements(pgs);
        assertThat(player.getAchievements().size()).isEqualTo(NUMBER_OF_GOOSE_ACHIEVEMENTS);
    }

    @Test
    @Transactional
    public void testAssignLudoAchievementsToPlayer(){

        Player player = playerService.findPlayerById(TEST_PLAYER_ID).get();
        PlayerLudoStats pls = new PlayerLudoStats();
        pls.setPlayer(player);
        pls.setHasWon(40);
        pls.setWalkedSquares(200);
        pls.setEatenTokens(300);
        achievementService.checkLudoAchievements(pls);
        assertThat(player.getAchievements().size()).isEqualTo(NUMBER_OF_LUDO_ACHIEVEMENTS);

    }
}
