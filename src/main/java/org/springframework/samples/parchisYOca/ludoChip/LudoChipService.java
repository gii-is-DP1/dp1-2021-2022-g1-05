package org.springframework.samples.parchisYOca.ludoChip;

import org.hibernate.envers.tools.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoardService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchRepository;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;
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
	private static final List<Integer> SAVE_TILES = Arrays.asList(4,11,16,21,28,33,38,45,50,55,62,67);
    private static final Integer FIVE = 5;
    private static final Map<Color, Integer> FIRST_TILES = Map.of(Color.Rojo, 38, Color.Amarillo, 4, Color.Verde, 55, Color.Azul, 21);


	private LudoChipRepository ludoChipRepository;
    private LudoBoardService ludoBoardService;
	private LudoMatchService ludoMatchService;
    private PlayerLudoStatsRepository playerLudoStatsRepository;


    @Autowired
	public LudoChipService(LudoChipRepository ludoChipRepository, LudoBoardService ludoBoardService, PlayerLudoStatsRepository playerLudoStatsRepository,
                           LudoMatchService ludoMatchService) {
		this.ludoChipRepository = ludoChipRepository;
        this.ludoBoardService = ludoBoardService;
        this.ludoMatchService = ludoMatchService;
        this.playerLudoStatsRepository=playerLudoStatsRepository;

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
        List<LudoChip> chips = new ArrayList<>();
        LudoBoard board=ludoMatchService.findludoMatchById(matchId).get().getBoard();
        List<LudoChip> allChips=new ArrayList<>(board.getChips());
        for(int i =0;i<allChips.size();i++){
            if(allChips.get(i).getInGamePlayerId()==inGameId&&allChips.get(i).getGameState().equals(GameState.earlyGame)){
                chips.add(allChips.get(i));
            }

        }

        if(!chips.isEmpty()) {
            LudoChip chipToModify = chips.get(0);
            Color color = chipToModify.getColor();

            if(!checkCasilla(FIRST_TILES.get(color), allChips).getFirst()) {
                Integer result = diceFlag(firstDice, secondDice);
                if(result >= 0 && result < 3) {
                    chipToModify.setGameState(GameState.midGame);
                    chipToModify.setPosition(FIRST_TILES.get(color));
                    save(chipToModify);
                } else {
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
            return 0;
        } else if(secondDice == FIVE) {
            return 1;
        } else if(firstDice+secondDice == FIVE) {
            return 2;
        } else if(firstDice == FIVE && secondDice == FIVE) {
            return 3;
        }
        return -1;
    }

    @Transactional
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
}
