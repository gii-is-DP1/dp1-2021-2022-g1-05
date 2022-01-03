package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public interface LudoMatchRepository extends CrudRepository<LudoMatch, Integer> {

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.matchCode = :matchCode")
    Optional<LudoMatch> findMatchByMatchCode(String matchCode);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch JOIN FETCH ludoMatch.stats stats WHERE stats.player.user.username = :username AND ludoMatch.endDate is null")
    Optional<LudoMatch> findLobbyByUsername(String username);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch JOIN FETCH ludoMatch.stats stats WHERE stats.player.user.username = :username")
    Collection<LudoMatch> findMatchesByUsername(String username);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.startDate > :date")
    Collection<LudoMatch> findLudoMatchByStartDate(Date date);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.endDate > :date")
    Collection<LudoMatch> findLudoMatchByEndDate(Date date);
}
