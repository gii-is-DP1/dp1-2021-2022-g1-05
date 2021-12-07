package org.springframework.samples.parchisYOca.gooseChip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class GooseChipService {

    private GooseChipRepository gooseChipRepository;

    @Autowired
    public GooseChipService(GooseChipRepository gooseChipRepository){
        this.gooseChipRepository = gooseChipRepository;
    }

    @Transactional(readOnly = true)
    public Optional<GooseChip> findById(Integer id) throws DataAccessException {
        return gooseChipRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Collection<GooseChip> findChipsByMatchId(Integer matchId) throws DataAccessException {
        return gooseChipRepository.findChipsByMatchId(matchId);
    }

    @Transactional
    public GooseChip save(GooseChip gooseChip) throws DataAccessException{
        gooseChipRepository.save(gooseChip);
        return gooseChip;
    }
}
