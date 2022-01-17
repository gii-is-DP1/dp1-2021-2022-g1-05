package org.springframework.samples.parchisYOca.gooseBoard;


import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseBoardServiceTest {

    protected final Integer NUMBER_OF_PLAYERS = 4;
    protected final Integer INVALID_NUMBER_OF_PLAYERS_1 = 6;
    protected final Integer INVALID_NUMBER_OF_PLAYERS_2 = -2;

    @Autowired
    protected GooseBoardService gooseBoardService;

    @Test
    @Transactional
    public void testSave() throws InvalidPlayerNumberException {
        GooseBoard aux=new GooseBoard();
        GooseBoard gooseBoardDb=gooseBoardService.save(aux,NUMBER_OF_PLAYERS);
        Assertions.assertThat(gooseBoardDb).isEqualTo(gooseBoardService.findById(gooseBoardDb.getId()).get());
    }
    @Test
    @Transactional
    public void testIncorrectSave(){
        GooseBoard aux=new GooseBoard();
        assertThrows(InvalidPlayerNumberException.class, () ->{
            gooseBoardService.save(aux,INVALID_NUMBER_OF_PLAYERS_1);
        });
        assertThrows(InvalidPlayerNumberException.class, () ->{
            gooseBoardService.save(aux,INVALID_NUMBER_OF_PLAYERS_2);
        });
    }

}
