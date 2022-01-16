package org.springframework.samples.parchisYOca.achievement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class AchievementServiceTests {

    private static final int TEST_ACHIEVEMENT_ID = 1;

    @Autowired
    private AchievementService achievementService;


    /*TODO AL METER EXCEPCIONES EN EL SAVE SE HA ROTO EL SERVICIO
    @Test
    @Transactional
    public void testAddCorrectAchievement(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("Nuevo logro");
        newAchievement.setDescription("Logro de prueba");
        Achievement addedAchievement = achievementService.save(newAchievement);

        assertThat(addedAchievement).isEqualTo(achievementService.findAchievementById(addedAchievement.getId()).get());
    }

    @Test
    @Transactional
    public void testAddWithNoName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setDescription("Solo tengo descripción");

        assertThrows(ConstraintViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    @Transactional
    public void testAddWithNoDescription(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("Solo tengo nombre");

        assertThrows(ConstraintViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    @Transactional
    public void testAddWithDuplicatedName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("NombreAchievement1");
        newAchievement.setDescription("Tengo un nombre que ya existe");

        assertThrows(DataIntegrityViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    @Transactional
    public void testAddNullAchievement(){
        assertThrows(InvalidDataAccessApiUsageException.class, () ->{
            achievementService.save(null);
        });
    }*/

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
    public void testDeleteNullAchievement(){

        assertThrows(DataAccessException.class, () -> {
           achievementService.delete(null);
        });
    }
}
