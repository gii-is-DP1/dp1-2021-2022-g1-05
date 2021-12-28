package org.springframework.samples.parchisYOca.gooseBoard;


import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseBoardServiceTest {

    protected final Integer NUMBER_OF_PLAYERS = 4;
    @Autowired
    protected GooseBoardService gooseBoardService;

    @Test
    @Transactional
    public void findById(){
        GooseBoard aux=new GooseBoard();
        GooseBoard gooseBoardDb=gooseBoardService.save(aux,NUMBER_OF_PLAYERS);
        Assertions.assertThat(gooseBoardService.findById(gooseBoardDb.getId()).isPresent());
    }

    @Test
    @Transactional
    public void testSave(){
        GooseBoard aux=new GooseBoard();
        GooseBoard gooseBoardDb=gooseBoardService.save(aux,NUMBER_OF_PLAYERS);
        Assertions.assertThat(gooseBoardDb).isEqualTo(gooseBoardService.findById(gooseBoardDb.getId()).get());
    }

}
