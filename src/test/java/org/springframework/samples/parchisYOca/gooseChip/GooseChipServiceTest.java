package org.springframework.samples.parchisYOca.gooseChip;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardService;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.gooseChip.exceptions.InvalidChipPositionException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseChipServiceTest {
    @Autowired
    protected GooseChipService gooseChipService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected GooseMatchService gooseMatchService;
    @Autowired
    protected GooseBoardService gooseBoardService;

    protected final Integer EXPECTED_CHIP_NUMBER_FOR_2_PLAYERS=2;
    protected final Integer MATCH_CODE_LENGTH=6;
    protected final Integer INVALID_POSITION=-3;
    protected final Integer FIRST_GOOSE=5;
    protected final Integer SECOND_GOOSE=9;
    protected final Integer THIRD_TO_LAST_GOOSE = 54;
    protected final Integer SECOND_TO_LAST_GOOSE = 59;
    protected final Integer LOSES_1_TURN=-1;
    protected final Integer LOSES_2_TURNS=-2;
    protected final Integer LAST_GOOSE=63;
    protected final Boolean DOBLES_SI=true;
    protected final Boolean DOBLES_NO=false;
    protected final Integer DEATH=58;
    protected final Integer JAIL=56;
    protected final Integer BRIDGE=6;
    protected final Integer INN=19;
    protected final Integer SECOND_BRIDGE=12;
    protected final Integer MAZE=42;
    protected final Integer END_MAZE=30;
    protected final Integer DICE=26;
    protected final Integer SECOND_DICE=53;
    protected final Integer FIRST_SQUARE=0;
    protected final Integer LAST_SQUARE=63;
    protected final Integer NOT_ESPECIAL_SQUARE =2;
    protected final Integer HAS_TURN =1;
    protected final Integer BOUNCE_2 =65;

    @Test
    @Transactional
    public void testFindByMatchId() throws InvalidPlayerNumberException {
        GooseMatch newMatch = new GooseMatch();
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
        GooseMatch addedMatch = gooseMatchService.saveGooseMatchWithPlayer(newMatch, addedPlayer, true);
        GooseMatch addedMatch2=gooseMatchService.saveGooseMatchWithPlayer(addedMatch, addedPlayer2, false);

        GooseBoard board = new GooseBoard();
        GooseBoard savedBoard = gooseBoardService.save(board, addedMatch2.getStats().size());
        addedMatch2.setBoard(savedBoard);

        Assertions.assertThat(gooseChipService.findChipsByMatchId(addedMatch2.getId()).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_2_PLAYERS);

    }

    @Test
    @Transactional
    public void testSave() throws InvalidChipPositionException {
        GooseChip chip1=new GooseChip();
        GooseChip savedChip=gooseChipService.save(chip1);
        Assertions.assertThat(savedChip).isEqualTo(gooseChipService.findById(savedChip.getId()).get());

    }
    @Test
    @Transactional
    public void testInvalidSave() {
        GooseChip chip1=new GooseChip();
        chip1.setPosition(INVALID_POSITION);

        assertThrows(InvalidChipPositionException.class, () ->{
            gooseChipService.save(chip1);
        });
    }

    @Test
    @Transactional
    public void testCheckSpecials() throws InvalidPlayerNumberException {

        GooseMatch newMatch = new GooseMatch();
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

        Player player2 = new Player();
        player2.setEmail("carmeeeeen@domain.com");
        User user2 = new User();
        user2.setUsername("Carmen12");
        user2.setPassword("Carmen111112");
        user2.setEnabled(true);
        player2.setUser(user2);
        Player addedPlayer2 = playerService.savePlayer(player2);


        GooseMatch addedMatch = gooseMatchService.saveGooseMatchWithPlayer(newMatch, addedPlayer, true);
        GooseMatch addedMatch2 = gooseMatchService.saveGooseMatchWithPlayer(addedMatch, addedPlayer2, false);
        GooseBoard board = new GooseBoard();
        board.setMatch(addedMatch2);
        GooseBoard savedBoard = gooseBoardService.save(board, addedMatch2.getStats().size());
        addedMatch2.setBoard(savedBoard);
        gooseMatchService.save(addedMatch2);

        List<GooseChip> chips = new ArrayList<>(gooseChipService.findChipsByMatchId(addedMatch2.getId()));
        GooseChip gc1 = chips.get(0);

        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,FIRST_GOOSE,DOBLES_NO).getFirst()).isEqualTo(SECOND_GOOSE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,THIRD_TO_LAST_GOOSE,DOBLES_NO).getFirst()).isEqualTo(SECOND_TO_LAST_GOOSE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,BRIDGE,DOBLES_NO).getFirst()).isEqualTo(SECOND_BRIDGE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,SECOND_BRIDGE,DOBLES_NO).getFirst()).isEqualTo(BRIDGE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,DICE,DOBLES_NO).getFirst()).isEqualTo(SECOND_DICE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,INN,DOBLES_NO).getFirst()).isEqualTo(INN);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,INN,DOBLES_NO).getSecond()).isEqualTo(LOSES_1_TURN);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,MAZE,DOBLES_NO).getFirst()).isEqualTo(END_MAZE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,JAIL,DOBLES_NO).getFirst()).isEqualTo(JAIL);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,JAIL,DOBLES_NO).getSecond()).isEqualTo(LOSES_2_TURNS);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,DEATH,DOBLES_NO).getFirst()).isEqualTo(FIRST_SQUARE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,LAST_GOOSE,DOBLES_NO).getFirst()).isEqualTo(LAST_SQUARE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,LAST_SQUARE,DOBLES_NO).getFirst()).isEqualTo(LAST_SQUARE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,BOUNCE_2,DOBLES_NO).getFirst()).isEqualTo(LAST_SQUARE-2);

        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,NOT_ESPECIAL_SQUARE,DOBLES_NO).getFirst()).isEqualTo(NOT_ESPECIAL_SQUARE);
        Assertions.assertThat(gooseChipService.checkSpecials("Carmen",gc1,NOT_ESPECIAL_SQUARE,DOBLES_SI).getSecond()).isEqualTo(HAS_TURN);


    }


}
