package org.springframework.samples.parchisYOca.ludoChip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoardService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LudoChipServiceTests {

    @Autowired
    protected LudoChipService ludoChipService;
    @Autowired
    protected PlayerService playerService;
    @Autowired
    protected LudoMatchService ludoMatchService;
    @Autowired
    protected LudoBoardService ludoBoardService;
    @Autowired
    protected PlayerLudoStatsService playerLudoStatsService;

    protected final Integer MATCH_CODE_LENGTH=6;
    protected final Integer EXPECTED_CHIP_NUMBER_FOR_NONEXISTENT_PLAYER=0;
    protected final Integer EXPECTED_CHIP_NUMBER_FOR_1_PLAYER=4;
    protected final Integer EXPECTED_CHIP_NUMBER_FOR_2_PLAYERS=8;
    protected final Integer EXPECTED_CHIP_NUMBER_FOR_NONEXISTENT_MATCH=0;
    protected final Integer TEST_MATCH_ID=1;
    protected final Integer NONEXISTENT_MATCH_ID=235;
    protected final Integer RANDOM_IRRELEVANT_VALUE=251;
    protected final Integer MID_GAME_BOARD_LENGTH=68;
    protected final Integer PLAYER_1_IN_GAME_ID=0;
    protected final Integer PLAYER_2_IN_GAME_ID=1;
    protected final Integer NONEXISTENT_PLAYER_IN_GAME_ID=-1;
    protected final Integer FIRST_CHIP_IN_GAME_ID=0;
    protected final Integer SECOND_CHIP_IN_GAME_ID=1;
    protected final Integer THIRD_CHIP_IN_GAME_ID=2;
    protected final Integer FOURTH_CHIP_IN_GAME_ID=3;

    protected final Integer NO_OPERATION_MANAGE_FIVES=-1;
    protected final Integer PRIMER_DADO_5=0;
    protected final Integer SEGUNDO_DADO_5=1;
    protected final Integer SUMA_DADOS_5=2;
    protected final Integer DOS_DADOS_5=3;

    protected final Integer INCORRECT_USE_OF_MOVE=-1;
    protected final Integer NO_OPERATION_MOVE=0;
    protected final Integer ATE_CHIP=1;
    protected final Integer LANDED_FINAL=2;
    protected final Integer GOT_BLOCKED=3;
    //protected final Integer BLOCKED_AND_ATE=4;
    protected final Integer ENDED_THE_GAME=5;

    protected final Integer MOVEMENT_HAPPY_PATH=20;
    protected final Integer MOVEMENT_START_POSITION=29;
    protected final Integer BLOCK_CREATION_POSITION=32;
    protected final Integer ARBITRARY_DICE_ROLL=3;
    protected final Integer LAST_MID_GAME_TILE_FOR_RED_CHIP = 33;
    protected final Integer END_GAME_START_POSITION=3;
    protected final Integer FINAL_TILE=7;

    protected final Integer EXPECTED_CHIPS_TO_BREAK_WITH_ONE_BLOCK=2;
    protected final Integer EXPECTED_CHIPS_TO_BREAK_WITH_NO_BLOCKS=0;

    protected final Integer EXPECTED_DISPLACED_CHIPS_WITH_NO_BLOCKS=0;
    protected final Integer EXPECTED_DISPLACED_CHIPS_WITH_ONE_BLOCK=1;

    protected List<LudoChip> allChips;
    protected PlayerLudoStats pls;
    protected LudoChip chipToMove;

    @BeforeEach
    public void addPlayersAndMatch() throws InvalidPlayerNumberException {
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
        ludoMatchService.save(addedMatch2);

        allChips = new ArrayList<>(ludoChipService.findChipsByMatchId(TEST_MATCH_ID));
        pls = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(PLAYER_1_IN_GAME_ID, TEST_MATCH_ID).get();
        chipToMove = ludoChipService.findConcreteChip(TEST_MATCH_ID,FIRST_CHIP_IN_GAME_ID, PLAYER_1_IN_GAME_ID).get();
    }

    @Test
    @Transactional
    public void testFindByMatchId() {
        assertThat(ludoChipService.findChipsByMatchId(TEST_MATCH_ID).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_2_PLAYERS);
    }

    @Test
    @Transactional
    public void testFindByMatchIdNotExists() {
        assertThat(ludoChipService.findChipsByMatchId(NONEXISTENT_MATCH_ID).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_NONEXISTENT_MATCH);
    }

    @ParameterizedTest
    @Transactional
    @CsvSource({"0, 0", "3, 0", "0, 1", "3, 1"})
    public void testFindConcreteChip(Integer inGameChipId, Integer inGamePlayerId) {
        assertThat(ludoChipService.findConcreteChip(TEST_MATCH_ID, inGameChipId, inGamePlayerId).isPresent());
    }

    @ParameterizedTest
    @Transactional
    @CsvSource({"-1, 0, 0",
                " 1, 0, 2",
                " 1,-1, 1"
                })
    public void testFindConcreteChipIncorrectValues(Integer matchId, Integer inGameChipId, Integer inGamePlayerId) {
        assertThat(ludoChipService.findConcreteChip(matchId, inGameChipId, inGamePlayerId).isEmpty());
    }

    @Test
    @Transactional
    public void testGetChipsByInGamePlayerId() {
        assertThat(ludoChipService.getChipsByInGamePlayerId(TEST_MATCH_ID, PLAYER_1_IN_GAME_ID).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_1_PLAYER);
        assertThat(ludoChipService.getChipsByInGamePlayerId(TEST_MATCH_ID, PLAYER_2_IN_GAME_ID).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_1_PLAYER);
    }

    @Test
    @Transactional
    public void testGetChipsByInGamePlayerIdIncorrectValues() {
        assertThat(ludoChipService.getChipsByInGamePlayerId(NONEXISTENT_MATCH_ID, PLAYER_1_IN_GAME_ID).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_NONEXISTENT_MATCH);
        assertThat(ludoChipService.getChipsByInGamePlayerId(TEST_MATCH_ID, NONEXISTENT_PLAYER_IN_GAME_ID).size()).isEqualTo(EXPECTED_CHIP_NUMBER_FOR_NONEXISTENT_PLAYER);
    }

    @Test   //Surplrisingly enough, this one is not transactional
    public void testLudoChipEntityRestrictions() {
        LudoChip chipToSave = new LudoChip();
        chipToSave.setPosition(RANDOM_IRRELEVANT_VALUE);
        assertThat(chipToSave.getPosition()).isEqualTo(null);

        chipToSave.setGameState(GameState.midGame);
        chipToSave.setPosition(RANDOM_IRRELEVANT_VALUE);
        assertThat(chipToSave.getPosition()).isEqualTo(RANDOM_IRRELEVANT_VALUE%MID_GAME_BOARD_LENGTH);
    }

    @Test
    @Transactional
    public void testSaveLudoChip() {
        LudoChip chipToSave = new LudoChip();
        LudoChip savedChip = ludoChipService.save(chipToSave);
        assertThat(ludoChipService.findById(savedChip.getId()).isPresent());
    }

    @Test
    @Transactional
    public void testManageFives() {
        assertThat(ludoChipService.manageFives(PLAYER_1_IN_GAME_ID, TEST_MATCH_ID, RANDOM_IRRELEVANT_VALUE, RANDOM_IRRELEVANT_VALUE)).isEqualTo(NO_OPERATION_MANAGE_FIVES);
        assertThat(ludoChipService.manageFives(PLAYER_1_IN_GAME_ID, TEST_MATCH_ID, 5, RANDOM_IRRELEVANT_VALUE)).isEqualTo(PRIMER_DADO_5);
        assertThat(ludoChipService.manageFives(PLAYER_1_IN_GAME_ID, TEST_MATCH_ID, RANDOM_IRRELEVANT_VALUE, 5)).isEqualTo(SEGUNDO_DADO_5);
        assertThat(ludoChipService.manageFives(PLAYER_2_IN_GAME_ID, TEST_MATCH_ID, 2, 3)).isEqualTo(SUMA_DADOS_5);
        assertThat(ludoChipService.manageFives(PLAYER_2_IN_GAME_ID, TEST_MATCH_ID, 5, 5)).isEqualTo(DOS_DADOS_5);
    }

    @Test
    @Transactional
    public void testMoveInEarlyGame() {
         assertThat(ludoChipService.move(chipToMove, ARBITRARY_DICE_ROLL, allChips, pls, TEST_MATCH_ID)).isEqualTo(INCORRECT_USE_OF_MOVE);
    }

    @Test
    @Transactional
    public void testMoveMidGameNoSpecialOutcome() {
        //Setup de la situación
        chipToMove = setupChip(chipToMove,MOVEMENT_HAPPY_PATH, GameState.midGame);

        assertThat(ludoChipService.move(chipToMove, ARBITRARY_DICE_ROLL,allChips,pls,TEST_MATCH_ID)).isEqualTo(NO_OPERATION_MOVE);
        assertThat(chipToMove.getPosition()).isEqualTo(MOVEMENT_HAPPY_PATH+ ARBITRARY_DICE_ROLL);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.midGame);
    }

    @ParameterizedTest
    @ValueSource(ints = {3,6})  //Just in the block and after the block
    @Transactional
    public void testMoveMidGameWithBlockAhead(Integer diceRoll) {
        //Setup de la situación
        chipToMove = setupChip(chipToMove, MOVEMENT_START_POSITION, GameState.midGame);

        LudoChip blockingChip1 = ludoChipService.findConcreteChip(TEST_MATCH_ID, FIRST_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(blockingChip1, BLOCK_CREATION_POSITION, GameState.midGame);
        LudoChip blockingChip2 = ludoChipService.findConcreteChip(TEST_MATCH_ID, SECOND_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(blockingChip2, BLOCK_CREATION_POSITION, GameState.midGame);

        assertThat(ludoChipService.move(chipToMove, diceRoll, allChips, pls, TEST_MATCH_ID)).isEqualTo(GOT_BLOCKED);
        assertThat(chipToMove.getPosition()).isEqualTo(BLOCK_CREATION_POSITION-1);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.midGame);
    }

    @Test
    @Transactional
    public void testMoveMidGameEatChip() {
        //Setup de la situación
        chipToMove = setupChip(chipToMove, MOVEMENT_START_POSITION, GameState.midGame);

        LudoChip chipToEat = ludoChipService.findConcreteChip(TEST_MATCH_ID, FIRST_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(chipToEat, MOVEMENT_START_POSITION+ARBITRARY_DICE_ROLL, GameState.midGame);

        assertThat(ludoChipService.move(chipToMove, ARBITRARY_DICE_ROLL, allChips, pls, TEST_MATCH_ID)).isEqualTo(ATE_CHIP);
        assertThat(chipToMove.getPosition()).isEqualTo(MOVEMENT_START_POSITION+ARBITRARY_DICE_ROLL);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.midGame);
    }

    @ParameterizedTest
    @ValueSource(ints = {4,5,6})
    @Transactional
    public void testMoveMidGameEnterEndGame(Integer diceRoll) {
        //Setup de la situación
        chipToMove = setupChip(chipToMove,MOVEMENT_START_POSITION, GameState.midGame);

        assertThat(ludoChipService.move(chipToMove, diceRoll,allChips,pls,TEST_MATCH_ID)).isEqualTo(NO_OPERATION_MOVE);
        assertThat(chipToMove.getPosition()).isEqualTo(MOVEMENT_START_POSITION+diceRoll-LAST_MID_GAME_TILE_FOR_RED_CHIP);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.endGame);

    }

    @Test
    @Transactional
    public void testMoveEndGameNoSpecialOutcome() {
        //Setup de la situación
        chipToMove = setupChip(chipToMove,END_GAME_START_POSITION, GameState.endGame);

        assertThat(ludoChipService.move(chipToMove, ARBITRARY_DICE_ROLL,allChips,pls,TEST_MATCH_ID)).isEqualTo(NO_OPERATION_MOVE);
        assertThat(chipToMove.getPosition()).isBetween(0, FINAL_TILE);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.endGame);
    }

    @Test
    @Transactional
    public void testMoveEndGameLandFinal() {
        //Setup de la situación
        chipToMove = setupChip(chipToMove,FINAL_TILE-ARBITRARY_DICE_ROLL, GameState.endGame);

        assertThat(ludoChipService.move(chipToMove, ARBITRARY_DICE_ROLL,allChips,pls,TEST_MATCH_ID)).isEqualTo(LANDED_FINAL);
        assertThat(chipToMove.getPosition()).isEqualTo(FINAL_TILE);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.endGame);
    }

    @Test
    @Transactional
    public void testMoveEndGameEndTheMatch() {
        //Setup de la situación
        chipToMove = setupChip(chipToMove,FINAL_TILE-ARBITRARY_DICE_ROLL, GameState.endGame);

        LudoChip finalChip1 = ludoChipService.findConcreteChip(TEST_MATCH_ID,SECOND_CHIP_IN_GAME_ID, PLAYER_1_IN_GAME_ID).get();
        setupChip(finalChip1,FINAL_TILE, GameState.endGame);
        LudoChip finalChip2 = ludoChipService.findConcreteChip(TEST_MATCH_ID,THIRD_CHIP_IN_GAME_ID, PLAYER_1_IN_GAME_ID).get();
        setupChip(finalChip2,FINAL_TILE, GameState.endGame);
        LudoChip finalChip3 = ludoChipService.findConcreteChip(TEST_MATCH_ID,FOURTH_CHIP_IN_GAME_ID, PLAYER_1_IN_GAME_ID).get();
        setupChip(finalChip3,FINAL_TILE, GameState.endGame);

        assertThat(ludoChipService.move(chipToMove, ARBITRARY_DICE_ROLL,allChips,pls,TEST_MATCH_ID)).isEqualTo(ENDED_THE_GAME);
        assertThat(chipToMove.getPosition()).isEqualTo(FINAL_TILE);
        assertThat(chipToMove.getGameState()).isEqualTo(GameState.endGame);
    }

    @Test
    @Transactional
    public void testNoChipsOutOfHome(){
        assertThat(ludoChipService.noChipsOutOfHome(allChips, PLAYER_1_IN_GAME_ID));
    }

    @Test
    @Transactional
    public void testNoChipsOutOfHomeIsFalse() {
        setupChip(chipToMove,MOVEMENT_START_POSITION, GameState.endGame);

        assertThat(ludoChipService.noChipsOutOfHome(allChips, PLAYER_1_IN_GAME_ID)).isFalse();
    }

    @Test
    @Transactional
    public void testBreakBlocks() {
        LudoChip blockingChip1 = ludoChipService.findConcreteChip(TEST_MATCH_ID, FIRST_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(blockingChip1, BLOCK_CREATION_POSITION, GameState.midGame);
        LudoChip blockingChip2 = ludoChipService.findConcreteChip(TEST_MATCH_ID, SECOND_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(blockingChip2, BLOCK_CREATION_POSITION, GameState.midGame);

        assertThat(ludoChipService.breakBlocks(allChips, PLAYER_2_IN_GAME_ID).size()).isEqualTo(EXPECTED_CHIPS_TO_BREAK_WITH_ONE_BLOCK);
    }

    @Test
    @Transactional
    public void testBreakBlocksWithNoBlocksToBreak() {
        assertThat(ludoChipService.breakBlocks(allChips, PLAYER_2_IN_GAME_ID).size()).isEqualTo(EXPECTED_CHIPS_TO_BREAK_WITH_NO_BLOCKS);
    }

    @Test
    @Transactional
    public void testManageGreedy() {
        setupChip(chipToMove, MOVEMENT_START_POSITION, GameState.midGame);
        PlayerLudoStats pls = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(PLAYER_2_IN_GAME_ID, TEST_MATCH_ID).get();
        pls.setLastChipMovedId(FIRST_CHIP_IN_GAME_ID);
        pls = playerLudoStatsService.saveStats(pls);

        ludoChipService.manageGreedy(TEST_MATCH_ID,pls);
        LudoChip chipAfterManageGreedy = ludoChipService.findConcreteChip(TEST_MATCH_ID, FIRST_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        assertThat(chipAfterManageGreedy.getGameState()).isEqualTo(GameState.earlyGame);
        assertThat(chipAfterManageGreedy.getPosition()).isEqualTo(null);
    }

    @Test
    @Transactional
    public void testCheckOccupiedWithNoBlocks() {
        assertThat(ludoChipService.checkOcuppied(allChips).size()).isEqualTo(EXPECTED_DISPLACED_CHIPS_WITH_NO_BLOCKS);
    }

    @Test
    @Transactional
    public void testCheckOccupied() {
        LudoChip blockingChip1 = ludoChipService.findConcreteChip(TEST_MATCH_ID, FIRST_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(blockingChip1, BLOCK_CREATION_POSITION, GameState.midGame);
        LudoChip blockingChip2 = ludoChipService.findConcreteChip(TEST_MATCH_ID, SECOND_CHIP_IN_GAME_ID, PLAYER_2_IN_GAME_ID).get();
        setupChip(blockingChip2, BLOCK_CREATION_POSITION, GameState.midGame);

        assertThat(ludoChipService.checkOcuppied(allChips).size()).isEqualTo(EXPECTED_DISPLACED_CHIPS_WITH_ONE_BLOCK);
    }

    private LudoChip setupChip(LudoChip chip, Integer position, GameState gameState) {
        chip.setGameState(gameState);
        chip.setPosition(position);
        return ludoChipService.save(chip);
    }

}
