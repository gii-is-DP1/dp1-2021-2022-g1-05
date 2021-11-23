package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class GooseMatchService {
    @Autowired
    private GooseMatchRepository gMatchRepo;
    @Transactional
    public Integer gMatchCount() {
        return (int) gMatchRepo.count();
    }
    @Transactional(readOnly = true)
    public Iterable<GooseMatch> findAll() throws DataAccessException {
        return gMatchRepo.findAll();
    }
    @Transactional
    public void gMatchCountActive() {
        /*Iterable<GooseMatch> gooseMatches =  gMatchRepo.findAll();
        List<GooseMatch> active = StreamSupport.stream(gooseMatches, false)
            .filter(GooseMatch.getEndDate()!=null);*/
    }
    @Transactional
    public  void gMatchCountFinnished() {
        //query para comparar partidas con fecha de fin o con ganador
    }
    @Transactional
    public void gMatchPlayerCount() {
        //query para obtener la cantidad de jugadores en una partida
    }
}
