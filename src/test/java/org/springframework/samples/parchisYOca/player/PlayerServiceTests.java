package org.springframework.samples.parchisYOca.player;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PlayerServiceTests {

    @Autowired
    protected PlayerService playerService;

    @Test
    @Transactional
    public void testInsertPlayer() {

        Player player = new Player();
        player.setEmail("pepe@gmail.com");
        User user = new User();
        user.setUsername("Pepe07");
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        Player addedplayer = playerService.savePlayer(player);
        assertThat(addedplayer).isEqualTo(playerService.findPlayerById(addedplayer.getId()));
    }

    @Test
    public void testInsertPlayerWithoutEmail(){
        Player player = new Player();
        User user = new User();
        user.setUsername("Pepe08");
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        assertThrows(ConstraintViolationException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    public void testInsertPlayerWithoutUsername(){
        Player player = new Player();
        player.setEmail("pepeReturns@gmail.com");
        User user = new User();
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        assertThrows(JpaSystemException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    public void testInsertPlayerWithWrongPassword(){
        Player player = new Player();
        player.setEmail("pepeIsUnstoppable@gmail.com");
        User user = new User();
        user.setUsername("Pepe09");
        user.setPassword("notSoGoodPassword");
        user.setEnabled(true);
        player.setUser(user);

        assertThrows(ConstraintViolationException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    public void testAddWithDuplicatedName(){        //MEJORA POSIBLE: Crear Excepción propia que maneje nombres duplicados
        Player player = new Player();
        player.setEmail("pepeYouAreWrong@gmail.com");
        User user = new User();
        user.setUsername("ManuK");
        user.setPassword("SoGoodPassword3");
        user.setEnabled(true);
        player.setUser(user);


        assertThrows(DataIntegrityViolationException.class, () ->{
            playerService.savePlayer(player);
        });
    }

    @Test
    public void testAddNullPlayer(){
        Player player = new Player();
        User user = new User();
        player.setUser(user);

        assertThrows(JpaSystemException.class, () ->{
            playerService.savePlayer(player);
        });
    }

}
