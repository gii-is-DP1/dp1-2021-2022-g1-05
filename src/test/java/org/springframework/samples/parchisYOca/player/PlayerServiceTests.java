package org.springframework.samples.parchisYOca.player;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class PlayerServiceTests {

    @Autowired
    protected PlayerService playerService;

    @Test
    @Transactional
    public void shouldInsertPlayer() {

        Player player = new Player();
        player.setEmail("pepe@gmail.com");
        User user=new User();
        user.setUsername("Pepe07");
        user.setPassword("supersecretpassword1");
        user.setEnabled(true);
        player.setUser(user);

        this.playerService.savePlayer(player);
        assertThat(player.getId().longValue()).isNotEqualTo(0);
    }

    @Test
    void shouldFindSinglePlayerWithTwoAchievements() {
        Player player = this.playerService.findPlayerById(1);
        assertThat(player.getUser().getUsername()).startsWith("ManuK");
        assertThat(player.getAchievements().size()).isEqualTo(2);
    }

}
