package org.springframework.samples.parchisYOca.achievement;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class AchievementServiceTests {
    @Autowired
    private AchievementService achievementService;

    @Test
    @Disabled("No poner datos absolutos, no se debería saber que el total son 3")
    public void testCountWithInitialData(){
        int count = achievementService.achievementCount();
        assertEquals(count,3);
    }

    @Test
    public void testFindByIdExists(){
        Optional<Achievement> achievement2 = achievementService.findAchievementById(1);
        assertNotEquals(achievement2.get(), Optional.empty());
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
        assertNotEquals(achievementService.findAchievementById(4), Optional.empty());
    }

    @Test
    public void testAddWithNoName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setDescription("Solo tengo descripción");

        assertThrows(NullPointerException.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    public void testAddWithNoDescription(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("Solo tengo nombre");

        assertThrows(ConstraintViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    public void testAddWithDuplicatedName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("NombreAchievement1");
        newAchievement.setDescription("Tengo un nombre que ya existe");

        assertThrows(Exception.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    public void testAddNullAchievement(){
        assertThrows(NullPointerException.class, () ->{
            achievementService.save(null);
        });
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

    @Test
    public void testDeleteNullAchievement(){

        assertThrows(DataAccessException.class, () -> {
           achievementService.delete(null);
        });
    }
}
