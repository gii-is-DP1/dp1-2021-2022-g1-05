package org.springframework.samples.parchisYOca.achievement;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class AchievementServiceTests {
    @Autowired
    private AchievementService achievementService;


    @Test
    public void testAddCorrectAchievement(){
        Achievement newAchievement = new Achievement();
        newAchievement.setName("Nuevo logro");
        newAchievement.setDescription("Logro de prueba");
        Achievement addedAchievement = achievementService.save(newAchievement);

        assertThat(addedAchievement).isEqualTo(achievementService.findAchievementById(addedAchievement.getId()));
    }

    @Test
    public void testAddWithNoName(){
        Achievement newAchievement = new Achievement();
        newAchievement.setDescription("Solo tengo descripción");

        assertThrows(ConstraintViolationException.class, () ->{
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
    public void testAddWithDuplicatedName(){        //MEJORA POSIBLE: Crear Excepción propia que maneje nombres duplicados
        Achievement newAchievement = new Achievement();
        newAchievement.setName("NombreAchievement1");
        newAchievement.setDescription("Tengo un nombre que ya existe");

        assertThrows(DataIntegrityViolationException.class, () ->{
            achievementService.save(newAchievement);
        });
    }

    @Test
    public void testAddNullAchievement(){
        assertThrows(InvalidDataAccessApiUsageException.class, () ->{
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
