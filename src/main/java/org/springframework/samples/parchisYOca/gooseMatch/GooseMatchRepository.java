package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface GooseMatchRepository extends CrudRepository<GooseMatch, Integer> {

    //TO-DO RN: Not join or create match when one is running
    //@Query(value = "SELECT gooseMatch FROM GooseMatch gooseMatch JOIN FETCH gooseMatch.stats stats WHERE gooseMatch.startDate <> null AND gooseMatch.endDate = null")
    //Collection<GooseMatch> findCurrentMatchesByPlayerId(Integer playerId) throws DataAccessException;

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.matchCode = :matchCode")
    Optional<GooseMatch> findMatchByMatchCode(String matchCode);

}
