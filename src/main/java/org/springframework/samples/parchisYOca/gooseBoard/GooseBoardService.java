package org.springframework.samples.parchisYOca.gooseBoard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
public class GooseBoardService {

    private GooseBoardRepository gooseBoardRepository;
    private GooseChipRepository gooseChipRepository;

    @Autowired
    public GooseBoardService(GooseBoardRepository gooseBoardRepository, GooseChipRepository gooseChipRepository){
        this.gooseBoardRepository = gooseBoardRepository;
        this.gooseChipRepository = gooseChipRepository;
    }

    @Transactional(readOnly = true)
    public Optional<GooseBoard> findById(Integer id) throws DataAccessException {
        return gooseBoardRepository.findById(id);
    }

    @Transactional
    public GooseBoard save(GooseBoard gooseBoard, Integer numberOfPlayers) throws DataAccessException, InvalidPlayerNumberException {

        GooseBoard gooseBoardDB = gooseBoardRepository.save(gooseBoard);

        if(numberOfPlayers>4||numberOfPlayers<=1){
            throw new InvalidPlayerNumberException();
        }
        for (Integer i = 0; i < numberOfPlayers; i++){
            GooseChip gooseChip = new GooseChip();
            gooseChip.setInGameId(i);
            gooseChip.setBoard(gooseBoardDB);
            gooseChipRepository.save(gooseChip);
        }

        return gooseBoardDB;
    }
}
