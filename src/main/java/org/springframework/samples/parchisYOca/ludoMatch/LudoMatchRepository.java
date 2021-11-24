package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface LudoMatchRepository extends CrudRepository<LudoMatch, Integer> {

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.matchCode = :matchCode AND ludoMatch.startDate IS null")
    Optional<LudoMatch> findMatchByMatchCode(String matchCode);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch JOIN FETCH ludoMatch.stats stats WHERE ludoMatch.startDate IS null AND stats.player.user.username = :username")
    Collection<LudoMatch> findLobbyByUsername(String username);
}
