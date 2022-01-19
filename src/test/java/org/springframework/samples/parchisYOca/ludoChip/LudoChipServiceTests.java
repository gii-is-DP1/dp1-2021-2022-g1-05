package org.springframework.samples.parchisYOca.ludoChip;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoardService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class LudoChipServiceTests {

    @Autowired
    protected LudoChipService ludoChipService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected LudoMatchService ludoMatchService;
    @Autowired
    protected LudoBoardService ludoBoardService;

    protected final Integer MATCH_CODE_LENGTH=6;
    protected final Integer EXPECTED_CHIP_NUMBER_FOR_2_PLAYERS=8;

    @Test
    @Transactional
    public void testFindByMatchId() throws InvalidPlayerNumberException {
        LudoMatch newMatch = new LudoMatch();
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        newMatch.setMatchCode(matchCode);
        newMatch.setStartDate(new Date());
        //Player 1
        Player player = new Player();
        player.setEmail("carmen@domain.com");
        User user = new User();
        user.setUsername("Carmen");
        user.setPassword("Carmen1111");
        user.setEnabled(true);
        player.setUser(user);
        Player addedPlayer = playerService.savePlayer(player);
        //Player2
        Player player2 = new Player();
        player2.setEmail("carmeeeeen@domain.com");
        User user2 = new User();
        user2.setUsername("Carmen12");
        user2.setPassword("Carmen111112");
        user2.setEnabled(true);
        player2.setUser(user2);

        Player addedPlayer2 = playerService.savePlayer(player2);
        LudoMatch addedMatch = ludoMatchService.saveludoMatchWithPlayer(newMatch, addedPlayer, true);
        LudoMatch addedMatch2=ludoMatchService.saveludoMatchWithPlayer(addedMatch, addedPlayer2, false);

        LudoBoard board = new LudoBoard();
        LudoBoard savedBoard = ludoBoardService.save(board, addedMatch2.getStats());
        addedMatch2.setBoard(savedBoard);

        Assertions.assertThat(ludoChipService.findChipsByMatchId(addedMatch2.getId()).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_2_PLAYERS);

    }

}
