package org.springframework.samples.parchisYOca.ludoChip;

import org.hibernate.envers.tools.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.util.Color;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class LudoChipService {
    private static final Integer RED_LAST_TILE = 33; //uno menos que en el tablero porque internamente vamos de 0-67
    private static final Integer YELLOW_LAST_TILE = 67;
    private static final Integer GREEN_LAST_TILE = 50;
    private static final Integer BLUE_LAST_TILE = 16;
    private static final List<Integer> SAFE_TILES = Arrays.asList(4,11,16,21,28,33,38,45,50,55,62,67);
    private static final Integer FIVE = 5;
    private static final Map<Color, Integer> FIRST_TILES = Map.of(Color.Red, 38, Color.Yellow, 4, Color.Green, 55, Color.Blue, 21);
    public static final Integer PRIMER_DADO_5=0;
    public static final Integer SEGUNDO_DADO_5=1;
    public static final Integer SUMA_DADOS_5=2;
    public static final Integer DOS_DADOS_5=3;


    private LudoChipRepository ludoChipRepository;
    private LudoMatchService ludoMatchService;


    @Autowired
    public LudoChipService(LudoChipRepository ludoChipRepository, LudoMatchService ludoMatchService) {
        this.ludoChipRepository = ludoChipRepository;
        this.ludoMatchService = ludoMatchService;

    }

    @Transactional
    public Optional<LudoChip> findConcreteChip(Integer matchId, Integer inGameChipId, Integer inGamePlayerId){
        return ludoChipRepository.findChip(matchId,inGameChipId,inGamePlayerId);
    }

    @Transactional
    public Collection<LudoChip> findChipsByMatchId(Integer matchId) throws DataAccessException {
        return ludoChipRepository.findChipsByMatchId(matchId);
    }

    @Transactional
    public LudoChip save(LudoChip ludoChip) {
        return ludoChipRepository.save(ludoChip);
    }

    @Transactional
    public Integer manageFives(Integer inGameId,Integer matchId, Integer firstDice, Integer secondDice){
        List<LudoChip> chipsInBase = new ArrayList<>();
        LudoBoard board=ludoMatchService.findludoMatchById(matchId).get().getBoard();
        List<LudoChip> allChips=new ArrayList<>(board.getChips());
        for(int i =0;i<allChips.size();i++){
            if(allChips.get(i).getInGamePlayerId()==inGameId&&allChips.get(i).getGameState().equals(GameState.earlyGame)){
                chipsInBase.add(allChips.get(i));
            }

        }


        if(!chipsInBase.isEmpty()) {
            LudoChip chipToModify = chipsInBase.get(0);
            Color color = chipToModify.getColor();

            if(!checkCasilla(FIRST_TILES.get(color), allChips).getFirst()) {
                Integer result = diceFlag(firstDice, secondDice);
                if(result >= 0 && result < 3) {
                    chipToModify.setGameState(GameState.midGame);
                    chipToModify.setPosition(FIRST_TILES.get(color));
                    save(chipToModify);
                } else if(result == 3){
                    chipToModify.setGameState(GameState.midGame);
                    chipToModify.setPosition(FIRST_TILES.get(color));
                    save(chipToModify);
                    manageFives(inGameId, matchId, 0, secondDice);
                }
                return result;
            }
        }
        return -1;
    }

    public Integer diceFlag(Integer firstDice, Integer secondDice) {
        if(firstDice == FIVE) {
            return PRIMER_DADO_5;
        } else if(secondDice == FIVE) {
            return SEGUNDO_DADO_5;
        } else if(firstDice+secondDice == FIVE) {
            return SUMA_DADOS_5;
        } else if(firstDice == FIVE && secondDice == FIVE) {
            return DOS_DADOS_5;
        }
        return -1;
    }

    public Pair<Boolean,Integer> checkCasilla(Integer square, List<LudoChip> chips){
        Integer acumulador=0;
        for(int i=0;i<chips.size();i++){
            if(acumulador==2){
                Pair<Boolean,Integer> result= new Pair(true,square);
                return result;
            }
            else if(chips.get(i).getPosition() == square && chips.get(i).getGameState() != GameState.earlyGame){
                acumulador++;
            }
        }
        return new Pair(false,null);

    }
//  todo gestionar los 20 extra por comer en el controlador
    public boolean move(LudoChip chip,Integer movements,List<LudoChip> chips,Integer inGamePlayerId){
        for(int i=0;i<movements;i++){
            if(checkCasilla(chip.getPosition()+i,chips).getFirst()) {
                chip.setPosition(chip.getPosition() + i - 1);
                save(chip);
                return eat(chip.getPosition()+i-1,chips,inGamePlayerId);
            }
        }
        chip.setPosition(chip.getPosition()+movements);
        save(chip);
        return eat(chip.getPosition()+movements,chips,inGamePlayerId);
    }

    public boolean eat(Integer square, List<LudoChip> chips,Integer inGamePlayerId){
        boolean result=false;
        List<LudoChip> otherPlayerChips = new ArrayList<>();
        for(LudoChip chip: chips){
            if(!(chip.getInGamePlayerId()==inGamePlayerId)){
                otherPlayerChips.add(chip);
            }
        }
        for(LudoChip enemyChip: otherPlayerChips){
            if(enemyChip.getPosition()==square && !SAFE_TILES.contains(enemyChip.getPosition())){
                enemyChip.setGameState(GameState.earlyGame);
                result=true;
            }
        }
        return result;
    }

}
