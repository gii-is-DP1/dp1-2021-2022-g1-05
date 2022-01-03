package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

public interface GooseMatchRepository extends CrudRepository<GooseMatch, Integer> {

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.matchCode = :matchCode")
    Optional<GooseMatch> findMatchByMatchCode(String matchCode);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch JOIN FETCH gooseMatch.stats stats WHERE stats.player.user.username = :username  AND gooseMatch.endDate is null")
    Optional<GooseMatch> findLobbyByUsername(String username);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch JOIN FETCH gooseMatch.stats stats WHERE stats.player.user.username = :username")
    Collection<GooseMatch> findMatchesByUsername(String username);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.startDate > :date")
    Collection<GooseMatch> findGooseMatchByStartDate(Date date);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.endDate > :date")
    Collection<GooseMatch> findGooseMatchByEndDate(Date date);




}
