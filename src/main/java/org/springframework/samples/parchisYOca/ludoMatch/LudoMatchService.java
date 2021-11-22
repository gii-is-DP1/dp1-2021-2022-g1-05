package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LudoMatchService {
    @Autowired
    private LudoMatchRepository ludoMatchRepository;

    @Transactional(readOnly = true)
    public int ludoMatchCount() throws DataAccessException {
        return (int) ludoMatchRepository.count();
    }

    @Transactional(readOnly = true)
    public Iterable<LudoMatch> findAll() throws DataAccessException{
        return ludoMatchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<LudoMatch> findLudoMatchById(int ludoMatchId) throws DataAccessException{
        return ludoMatchRepository.findById(ludoMatchId);
    }

    @Transactional
    public void save(LudoMatch ludoMatch) throws DataAccessException{
        ludoMatchRepository.save(ludoMatch);
    }


    public void delete(LudoMatch ludoMatch) throws DataAccessException{
        ludoMatchRepository.delete(ludoMatch);
    }
}
