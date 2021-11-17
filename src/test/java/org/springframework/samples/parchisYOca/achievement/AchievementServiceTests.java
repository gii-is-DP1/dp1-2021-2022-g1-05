package org.springframework.samples.parchisYOca.achievement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class AchievementServiceTests {
    @Autowired
    private AchievementService achievementService;

    @Test
    public void testCountWithInitialData(){
        int count = achievementService.achievementCount();
        assertEquals(count,3);
    }

    @Test
    public void testFindByIdExists(){
        Optional<Achievement> achievement2 = achievementService.findAchievementById(1);
        assertEquals(achievement2.get().getName(), "NombreAchievement1");
        assertEquals(achievement2.get().getDescription(), "Descripción achievement 1");
    }

    @Test
    public void testFindByIdNotExists(){
        Optional<Achievement> achievementEmpty = achievementService.findAchievementById(400);
        assertEquals(achievementEmpty, Optional.empty());
    }

    @Test
    public void testAddCorrectAchievement(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("Nuevo logro");
        newAchievement.setDescription("Logro de prueba");
        achievementService.save(newAchievement);
        assertNotEquals(achievementService.findAchievementById(4), Optional.empty());
    }

    @Test
    public void testDeleteExistingAchievement(){
        Optional<Achievement> achievementToDelete = achievementService.findAchievementById(3);
        if(achievementToDelete.equals(Optional.empty())) {
            fail();
        } else {
            achievementService.delete(achievementToDelete.get());
            assertEquals(achievementService.findAchievementById(3), Optional.empty());
        }
    }
}
