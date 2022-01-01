package org.springframework.samples.parchisYOca.gooseChip;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

//TODO echar un vistazo a @BeforeEach

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseChipTest {
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
    protected final Integer LAST_GOOSE=63;
    protected final Boolean DOBLES_SI=true;
    protected final Boolean DOBLES_NO=false;
    protected final Integer JAIL=56;
    protected final Integer BRIDGE=6;
    protected final Integer MAZE=42;
    protected final Integer DICE=26;

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

    //TODO parece que el error lo da la autenticación
    @Disabled
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

        //JUGADOR 2 PA TI MARIO
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
        gooseMatchService.save(addedMatch2);
        //A partir de aqui UwU

        List<GooseChip> chips=new ArrayList<>(gooseChipService.findChipsByMatchId(addedMatch2.getId()));
        GooseBoard tablero=gooseBoardService.findById(savedBoard.getId()).get();
        GooseChip gc1=chips.get(0);
        System.out.println(tablero.getChips());
        System.out.println(addedMatch2.getBoard().getChips());
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(gc1.getBoard().getMatch());

        gooseChipService.checkSpecials(gc1,FIRST_GOOSE,DOBLES_NO);
        Assertions.assertThat(gc1.getPosition()).isEqualTo(SECOND_GOOSE);

    }


}
