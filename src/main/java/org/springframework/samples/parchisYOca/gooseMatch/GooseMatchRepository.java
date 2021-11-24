package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface GooseMatchRepository extends CrudRepository<GooseMatch, Integer> {

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.matchCode = :matchCode AND gooseMatch.startDate IS null")
    Optional<GooseMatch> findMatchByMatchCode(String matchCode);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch JOIN FETCH gooseMatch.stats stats WHERE gooseMatch.startDate IS null AND stats.player.user.username = :username")
    Collection<GooseMatch> findLobbyByUsername(String username);
}
