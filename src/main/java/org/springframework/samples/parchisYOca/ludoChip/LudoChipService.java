package org.springframework.samples.parchisYOca.ludoChip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.util.Color;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class LudoChipService {

    private static final Map<Color,Integer> LAST_TILES = Map.of(Color.Red,33, Color.Yellow,67, Color.Green, 50,Color.Blue,16);
    private static final List<Integer> SAFE_TILES = Arrays.asList(4,11,16,21,28,33,38,45,50,55,62,67);
    private static final Integer FIVE = 5;
    private static final Map<Color, Integer> FIRST_TILES = Map.of(Color.Red, 38, Color.Yellow, 4, Color.Green, 55, Color.Blue, 21);
    public static final Integer PRIMER_DADO_5=0;
    public static final Integer SEGUNDO_DADO_5=1;
    public static final Integer SUMA_DADOS_5=2;
    public static final Integer DOS_DADOS_5=3;
    public static final Integer FINAL_TILE=7;
    public static final Integer NUMBER_OF_CHIPS_BY_PLAYER=4;

    public static final Integer NO_OPERATION=0;
    public static final Integer ATE_CHIP=1;
    public static final Integer LANDED_FINAL=2;
    public static final Integer GOT_BLOCKED=3;
    //public static final Integer BLOCKED_AND_ATE=4;
    public static final Integer ENDED_THE_GAME=5;

    private LudoChipRepository ludoChipRepository;
    private LudoMatchService ludoMatchService;
    private PlayerLudoStatsService playerLudoStatsService;


    @Autowired
    public LudoChipService(LudoChipRepository ludoChipRepository, LudoMatchService ludoMatchService, PlayerLudoStatsService playerLudoStatsService) {
        this.ludoChipRepository = ludoChipRepository;
        this.ludoMatchService = ludoMatchService;
        this.playerLudoStatsService = playerLudoStatsService;

    }

    @Transactional
    public Optional<LudoChip> findConcreteChip(Integer matchId, Integer inGameChipId, Integer inGamePlayerId){
    	log.debug("Finding chip with inGameId '{}', inGamePlayerId '{}' and matchId '{}'",inGameChipId,inGamePlayerId,matchId);
        return ludoChipRepository.findChip(matchId,inGameChipId,inGamePlayerId);
    }

    @Transactional
    public Collection<LudoChip> findChipsByMatchId(Integer matchId) throws DataAccessException {
    	log.debug("Finding chips from Ludo match with id '{}'", matchId);
        return ludoChipRepository.findChipsByMatchId(matchId);
    }

    @Transactional
    public LudoChip save(LudoChip ludoChip) {
    	log.debug("Saving Ludo chip with inGameId '{}' and inGamePlayerId '{}'",ludoChip.getInGameChipId()
    			,ludoChip.getInGamePlayerId());
        return ludoChipRepository.save(ludoChip);
    }

    @Transactional
    public Integer manageFives(Integer inGameId,Integer matchId, Integer firstDice, Integer secondDice){
    	log.debug("Deciding if player with ingameId '{}' in Ludo match with id '{}' should be forced to move their chip with these rolls '{}','{}'"
    			,inGameId,matchId, firstDice, secondDice);
        List<LudoChip> chipsInBase = new ArrayList<>();
        LudoBoard board=ludoMatchService.findludoMatchById(matchId).get().getBoard();
        List<LudoChip> allChips=new ArrayList<>(board.getChips());
        log.debug("Checking which of these ({}) chips are in base", allChips);
        for(int i =0;i<allChips.size();i++){
            if(allChips.get(i).getInGamePlayerId()==inGameId&&allChips.get(i).getGameState().equals(GameState.earlyGame)){
                chipsInBase.add(allChips.get(i));
            }

        }
        log.debug("{} are the chips that are in base", chipsInBase);

        if(!chipsInBase.isEmpty()) {
            LudoChip chipToModify = chipsInBase.get(0);
            Color color = chipToModify.getColor();
            if(!checkCasilla(FIRST_TILES.get(color), allChips)) {
                Integer result = diceFlag(firstDice, secondDice);
                log.debug("Checking if starting tile is not occupied");
                //Aumentar la estadística de fichas sacadas
                PlayerLudoStats pls = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(chipToModify.getInGamePlayerId(), matchId).get();
                pls.setTakeOuts(pls.getTakeOuts()+1);

                if(result >= 0 && result < 3) {
                	log.debug("Chip with id '{}' can move", chipToModify.getInGameChipId());
                    chipToModify.setGameState(GameState.midGame);
                    chipToModify.setPosition(FIRST_TILES.get(color));
                    save(chipToModify);
                } else if(result == DOS_DADOS_5){
                	log.debug("There were two 5 rolled so two chips must move");
                	log.debug("Chip with id '{}' can move", chipToModify.getInGameChipId());
                    chipToModify.setGameState(GameState.midGame);
                    chipToModify.setPosition(FIRST_TILES.get(color));
                    save(chipToModify);
                    pls.setDoubleRolls(pls.getDoubleRolls()+1);
                    manageFives(inGameId, matchId, 0, secondDice);
                }
                playerLudoStatsService.saveStats(pls);
                return result;
            }
        }
        return -1;
    }

    @Transactional(readOnly = true)
    public Collection<LudoChip> getChipsByInGamePlayerId(Integer inGameId){
        return ludoChipRepository.findChipsByInGamePlayerId(inGameId);
    }

    private Integer diceFlag(Integer firstDice, Integer secondDice) {
    	log.debug("Flaging the dices '{}','{}'", firstDice, secondDice);
        if(firstDice == FIVE) {
        	log.debug("First dice was 5");
            return PRIMER_DADO_5;
        } else if(secondDice == FIVE) {
        	log.debug("Second dice was 5");
            return SEGUNDO_DADO_5;
        } else if(firstDice+secondDice == FIVE) {
        	log.debug("The sum of both dices was 5");
            return SUMA_DADOS_5;
        } else if(firstDice == FIVE && secondDice == FIVE) {
        	log.debug("Both dices were 5");
            return DOS_DADOS_5;
        }
        return -1;
    }

    public Boolean checkCasilla(Integer square, List<LudoChip> chips){
    	log.debug("Checking if tile number '{}' is occupied by any chip", square);
        Integer acumulador=0;
        for(int i=0;i<chips.size();i++){
            if(acumulador==2){
                return true;
            }
            else if(chips.get(i).getPosition() == square && chips.get(i).getGameState() != GameState.earlyGame){
                acumulador++;
            }
        }
        return false;
    }

    private Boolean checkFinalTiles(Integer square, LudoChip chip){
        log.debug("Checking if tile number '{}' is the start of that colors endgame", square);
        return square==LAST_TILES.get(chip.getColor());
    }

    @Transactional
    public Integer move(LudoChip chip,Integer movements,List<LudoChip> chips,PlayerLudoStats pls, Integer matchId) {
        if(chip.getGameState()==GameState.midGame) {
            return moveMidGame(chip, movements, chips, pls, matchId);
        } else if(chip.getGameState()==GameState.endGame) {
            return moveEndGame(chip, movements, pls);
        }
        return -1;
    }

    @Transactional
    protected Integer moveMidGame(LudoChip chip,Integer movements,List<LudoChip> chips,PlayerLudoStats pls, Integer matchId){
        Integer inGamePlayerId=pls.getInGameId();
        pls.setLastChipMovedId(chip.getInGameChipId());
        for(int i=1;i<=movements;i++){
            if(checkCasilla(chip.getPosition()+i,chips)){
                chip.setPosition(chip.getPosition()+i-1);
                save(chip);
                pls.setWalkedSquares(pls.getWalkedSquares()+ i - 1);
                playerLudoStatsService.saveStats(pls);
                return GOT_BLOCKED + eat(chip.getPosition(),chips,inGamePlayerId, matchId);
            }
        }
        for(Integer j=0;j<=movements;j++) {
            if(checkFinalTiles(chip.getPosition()+j,chip)){
                chip.setGameState(GameState.endGame);
                chip.setPosition(0+movements-j-1);
                save(chip);
                pls.setWalkedSquares(pls.getWalkedSquares()+movements);
                playerLudoStatsService.saveStats(pls);
                return NO_OPERATION;
            }
        }
        chip.setPosition(chip.getPosition()+movements);
        save(chip);
        pls.setWalkedSquares(pls.getWalkedSquares()+movements);
        playerLudoStatsService.saveStats(pls);
        return eat(chip.getPosition(),chips,inGamePlayerId, matchId);
    }

    @Transactional
    protected Integer moveEndGame(LudoChip chip,Integer movements,PlayerLudoStats pls){
        pls.setLastChipMovedId(chip.getInGameChipId());
        chip.setPosition(chip.getPosition()+movements);
        save(chip);
        pls.setWalkedSquares(pls.getWalkedSquares()+movements);
        if(chip.getPosition()==FINAL_TILE) {
            pls.setScoredTokens(pls.getScoredTokens()+1);
        }

        List<LudoChip> thisPlayerChips = new ArrayList<>(getChipsByInGamePlayerId(pls.getInGameId()));

        Integer chipsInFinalTileAcum = 0;
        for(LudoChip chipToCheck: thisPlayerChips) {
            if(chipToCheck.getGameState()==GameState.endGame && chipToCheck.getPosition()==7) {
                chipsInFinalTileAcum++;
            }
        }
        if(chipsInFinalTileAcum==NUMBER_OF_CHIPS_BY_PLAYER) {
            pls.setHasWon(1);
            LudoMatch ludoMatch = ludoMatchService.findMatchByPlayerLudoStats(pls).get();
            ludoMatch.setEndDate(new Date());
            ludoMatchService.save(ludoMatch);
            playerLudoStatsService.saveStats(pls);
            return ENDED_THE_GAME;
        }
        playerLudoStatsService.saveStats(pls);
        if(chip.getPosition()==FINAL_TILE) {
            return LANDED_FINAL;
        }

        return NO_OPERATION;
    }

    @Transactional
    protected Integer eat(Integer square, List<LudoChip> chips,Integer inGamePlayerId, Integer matchId){
        Integer result=NO_OPERATION;
        List<LudoChip> otherPlayerChips = new ArrayList<>();
        for(LudoChip chip: chips){
            if(!(chip.getInGamePlayerId()==inGamePlayerId)){
                otherPlayerChips.add(chip);
            }
        }
        for(LudoChip enemyChip: otherPlayerChips){
            if(enemyChip.getPosition()==square && !SAFE_TILES.contains(enemyChip.getPosition()) && enemyChip.getGameState().equals(GameState.midGame)){
                enemyChip.setGameState(GameState.earlyGame);
                save(enemyChip);

                PlayerLudoStats pls = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(inGamePlayerId, matchId).get();
                pls.setEatenTokens(pls.getEatenTokens()+1);
                playerLudoStatsService.saveStats(pls);

                result=ATE_CHIP;
            }
        }
        return result;
    }

    public boolean noChipsOutOfHome(List<LudoChip> ludoChips, Integer inGameId) {
    	log.debug("Checking if any of the chips of player with inGameId '{}' are not in base", inGameId);
        boolean res=true;

        for(LudoChip chip:ludoChips){
            if(chip.getGameState()!=GameState.earlyGame&&chip.getInGamePlayerId()==inGameId){
                res=false;
            }
        }
        return res;
    }


    public List<LudoChip> checkOcuppied(List<LudoChip> chips){
        Set<LudoChip> toBeDisplaced = new HashSet<LudoChip>();
        List<LudoChip> displacedOnes = new ArrayList<LudoChip>();
        for(LudoChip chip:chips) {
        	for(LudoChip chip2:chips) {
        		if(chip.getPosition()==chip2.getPosition()) {
        			toBeDisplaced.add(chip);
        			toBeDisplaced.add(chip2);
        		}
        	}
        }
        for(LudoChip chip:toBeDisplaced) {
        	if(!displacedOnes.contains(chip)) {
        		Boolean checker = true;
        		for(LudoChip chip2:displacedOnes) {
        			if(chip.getPosition()==chip2.getPosition()) {
        				checker = false;
        			}
        		}
        		if(checker) {
        			displacedOnes.add(chip);
        		}
        	}
        }
        return displacedOnes;
    }

    public List<LudoChip> breakBlocks(List<LudoChip> ludoChips, Integer inGameId) {
    	log.debug("Checking if the chips of player with inGameId '{}' should break a block", inGameId);
        List<LudoChip> chipsToBreak = new ArrayList<>();
        for(LudoChip chipToCheck: ludoChips) {
            if(chipToCheck.getInGamePlayerId() == inGameId) {
                if (chipToCheck.getGameState().equals(GameState.midGame)) { // || chipToCheck.getPosition() != LudoChip.FINAL_TILE
                    if(checkCasilla(chipToCheck.getPosition(), ludoChips)) {
                        chipsToBreak.add(chipToCheck);
                    }
                }
            }
        }
        return chipsToBreak;
    }

    @Transactional
    public void ManageGreedy(PlayerLudoStats playerStats) {
        List<LudoChip> playerChips=new ArrayList<>(getChipsByInGamePlayerId(playerStats.getInGameId()));
        for(LudoChip chip: playerChips){
            if(chip.getInGameChipId()==playerStats.getLastChipMovedId()){
                chip.setGameState(GameState.earlyGame);
                save(chip);
            }
        }
    }
}
