package org.springframework.samples.parchisYOca.ludoMatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class LudoMatchServiceTests {

    @Autowired
    LudoMatchService ludoMatchService;

    @Test
    @Transactional
    public void getWithMatchCode(){
        assertThat(ludoMatchService.findludoMatchByMatchCode("111111").isPresent());
    }

    @Test
    @Transactional
    public void getWithWrongMatchCode(){
        assertThat(ludoMatchService.findludoMatchByMatchCode("").isEmpty());
    }

}

