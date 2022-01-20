package org.springframework.samples.parchisYOca.ludoBoard;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipRepository;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class LudoBoardService {
    private LudoBoardRepository ludoBoardRepository;
    private LudoChipRepository ludoChipRepository;
    private PlayerLudoStatsService playerLudoStatsService;

    @Autowired
    public LudoBoardService(LudoBoardRepository ludoBoardRepository, LudoChipRepository ludoChipRepository, PlayerLudoStatsService playerLudoStatsService){
        this.ludoBoardRepository=ludoBoardRepository;
        this.ludoChipRepository=ludoChipRepository;
        this.playerLudoStatsService = playerLudoStatsService;
    }

    @Transactional(readOnly = true)
    public Optional<LudoBoard> findById(Integer id) throws DataAccessException{
        return ludoBoardRepository.findById(id);
    }

    @Transactional(readOnly=true)
    public LudoBoard save(LudoBoard ludoBoard, Set<PlayerLudoStats> playerLudoStats) throws DataAccessException, InvalidPlayerNumberException {
        LudoBoard ludoBoardDb=ludoBoardRepository.save(ludoBoard);
        Set<LudoChip> chips = new HashSet<>();
        Integer playerNumber= playerLudoStats.size();
        if(playerNumber>4||playerNumber<=1){
            throw new InvalidPlayerNumberException();
        }
        for(Integer i =0;i<playerNumber;i++){
            //Crear las 4 fichas para cada jugador
            for(Integer j=0;j<=3;j++){
                LudoChip ludoChip=new LudoChip();
                ludoChip.setInGameChipId(j);
                ludoChip.setInGamePlayerId(i);
                ludoChip.setBoard(ludoBoardDb);
                ludoChip.setColor();
                LudoChip ludoChipDb = ludoChipRepository.save(ludoChip);
                chips.add(ludoChipDb);
            }
        }
        ludoBoardDb.setChips(chips);
        LudoBoard ludoBoardFinal = ludoBoardRepository.save(ludoBoardDb);
        return ludoBoardFinal;
    }

    @Transactional
    public boolean checkGreedy(PlayerLudoStats inGamePlayerStats, boolean flagDobles) {
        if(flagDobles){
            inGamePlayerStats.setDoubleRolls(inGamePlayerStats.getDoubleRolls()+1);
            inGamePlayerStats.setTurnDoubleRolls(inGamePlayerStats.getTurnDoubleRolls()+1);
            inGamePlayerStats = playerLudoStatsService.saveStats(inGamePlayerStats);
        }
        if(inGamePlayerStats.getTurnDoubleRolls()==3){
            inGamePlayerStats.setHasTurn(0);
            inGamePlayerStats.setTurnDoubleRolls(0);
            inGamePlayerStats.setGreedyRolls(inGamePlayerStats.getGreedyRolls()+1);
            playerLudoStatsService.saveStats(inGamePlayerStats);
            return true;
        }
        return false;
    }

}
