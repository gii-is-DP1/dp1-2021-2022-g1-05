package org.springframework.samples.parchisYOca.ludoChip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.stereotype.Service;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class LudoChipServiceTests {

    @Autowired
    protected LudoChipService ludoChipService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected LudoMatchService ludoMatchService;

}
