package org.springframework.samples.parchisYOca.ludoBoard;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LudoBoardService {
    private LudoBoardRepository ludoBoardRepository;
    private LudoChipRepository ludoChipRepository;
    //TODO Esto de aqui abajo revisarlo
    @Autowired
    public LudoBoardService(LudoBoardRepository ludoBoardRepository,LudoChipRepository ludoChipRepository){
        this.ludoBoardRepository=ludoBoardRepository;
        this.ludoChipRepository=ludoChipRepository;
    }
    @Transactional(readOnly = true)
    public LudoBoard getLudoBoardById(Integer id) throws DataAccessException{
        return ludoBoardRepository.findById(id).get();
    }

    @Transactional(readOnly=true)
    public LudoBoard save(LudoBoard ludoBoard,Integer playerNumber) throws DataAccessException {
        LudoBoard ludoBoardDb=ludoBoardRepository.save(ludoBoard);
        for(Integer i =1;i<=playerNumber;i++){
            //Crear las 4 fichas para cada jugador
            for(Integer j=1;j<=4;i++){
                LudoChip ludoChip=new LudoChip();
                ludoChip.setPosition(i);
                ludoChip.setPlayerId(i);
                ludoChip.setBoard(ludoBoardDb);
                ludoChipRepository.save(ludoChip);
            }
        }
        return ludoBoardDb;
    }


}
