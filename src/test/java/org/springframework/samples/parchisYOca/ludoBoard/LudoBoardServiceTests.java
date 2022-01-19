package org.springframework.samples.parchisYOca.ludoBoard;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class LudoBoardServiceTests {

    protected Integer NUMBER_OF_CHIPS_4_PLAYERS = 16;
    protected Integer ONE_DOUBLE_ROLL_THIS_TURN = 1;
    protected Integer TWO_DOUBLE_ROLLS_THIS_TURN = 2;
    protected Integer LOSES_TURN = 0;
    protected Integer NUMBER_OF_TOTAL_DOUBLE_ROLLS = 1;
    protected Integer NUMBER_OF_TOTAL_DOUBLE_ROLLS_TWO = 2;
    protected Integer NUMBER_OF_TOTAL_DOUBLE_ROLLS_THREE = 3;
    protected Integer DOESNT_HAVE_DOUBLE_ROLLS = 0;
    protected Boolean NO_DOUBLE_ROLL = false;
    protected Boolean GOT_DOUBLE_ROLL = true;

    @Autowired
    protected LudoBoardService ludoBoardService;
    @Autowired
    protected PlayerLudoStatsService playerLudoStatsService;


    @Test
    @Transactional
    public void testSave() {
        LudoBoard aux = new LudoBoard();
        PlayerLudoStats pls1 = new PlayerLudoStats();
        PlayerLudoStats pls2 = new PlayerLudoStats();
        PlayerLudoStats pls3 = new PlayerLudoStats();
        PlayerLudoStats pls4 = new PlayerLudoStats();
        Set<PlayerLudoStats> setStats = Set.of(pls1, pls2, pls3, pls4);

        LudoBoard ludoBoardDb = ludoBoardService.save(aux, setStats);
        Assertions.assertThat(ludoBoardDb).isEqualTo(ludoBoardService.findById(ludoBoardDb.getId()).get());
        Assertions.assertThat(ludoBoardService.findById(ludoBoardDb.getId()).get().getChips().size()).isEqualTo(NUMBER_OF_CHIPS_4_PLAYERS);
    }

    @Test
    @Transactional
    public void testSaveIncorrectNumberOfPlayers() {
        LudoBoard aux = new LudoBoard();
        PlayerLudoStats pls1 = new PlayerLudoStats();
        PlayerLudoStats pls2 = new PlayerLudoStats();
        PlayerLudoStats pls3 = new PlayerLudoStats();
        PlayerLudoStats pls4 = new PlayerLudoStats();
        PlayerLudoStats pls5 = new PlayerLudoStats();
        Set<PlayerLudoStats> setStats = Set.of(pls1, pls2, pls3, pls4, pls5);

        assertThrows(ArrayIndexOutOfBoundsException.class, () ->{
            ludoBoardService.save(aux, setStats);
        });
    }

    @Test
    @Transactional
    public void testCheckGreedyWithDoubleRollFlagAndNoGreedyRoll() {
        PlayerLudoStats pls = new PlayerLudoStats();
        pls.setTurnDoubleRolls(ONE_DOUBLE_ROLL_THIS_TURN);
        PlayerLudoStats plsDb = playerLudoStatsService.saveStats(pls);

        Assertions.assertThat(ludoBoardService.checkGreedy(pls, GOT_DOUBLE_ROLL)).isFalse();
        Assertions.assertThat(playerLudoStatsService.findById(plsDb.getId()).get().getDoubleRolls()).isEqualTo(NUMBER_OF_TOTAL_DOUBLE_ROLLS);
    }

    @Test
    @Transactional
    public void testCheckGreedyWithoutDoubleRollFlagAndNoGreedyRoll() {
        PlayerLudoStats pls = new PlayerLudoStats();
        pls.setTurnDoubleRolls(ONE_DOUBLE_ROLL_THIS_TURN);
        PlayerLudoStats plsDb = playerLudoStatsService.saveStats(pls);

        Assertions.assertThat(ludoBoardService.checkGreedy(pls, NO_DOUBLE_ROLL)).isFalse();
        Assertions.assertThat(playerLudoStatsService.findById(plsDb.getId()).get().getDoubleRolls()).isEqualTo(DOESNT_HAVE_DOUBLE_ROLLS);
    }

    @Test
    @Transactional
    public void testCheckGreedyWithoDoubleRollFlagAndGreedyRoll() {
        PlayerLudoStats pls = new PlayerLudoStats();
        pls.setTurnDoubleRolls(TWO_DOUBLE_ROLLS_THIS_TURN);
        pls.setDoubleRolls(NUMBER_OF_TOTAL_DOUBLE_ROLLS_TWO);
        PlayerLudoStats plsDb = playerLudoStatsService.saveStats(pls);

        Assertions.assertThat(ludoBoardService.checkGreedy(pls, GOT_DOUBLE_ROLL)).isTrue();
        Assertions.assertThat(playerLudoStatsService.findById(plsDb.getId()).get().getDoubleRolls()).isEqualTo(NUMBER_OF_TOTAL_DOUBLE_ROLLS_THREE);
        Assertions.assertThat(playerLudoStatsService.findById(plsDb.getId()).get().getHasTurn()).isEqualTo(LOSES_TURN);
    }


}
