package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;

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

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch JOIN gooseMatch.stats stats WHERE stats.player.user.username = :username")
    Page<GooseMatch> findMatchesByUsernameWithPaging(String username, Pageable pageable);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.startDate > :date")
    Page<GooseMatch> findGooseMatchByStartDate(Date date, Pageable pageable);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.endDate > :date")
    Page<GooseMatch> findGooseMatchByEndDate(Date date, Pageable pageable);

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE gooseMatch.endDate is not null")
    Collection<GooseMatch> findEndedGooseMatches();

    @Query(value = "SELECT DISTINCT gooseMatch FROM GooseMatch gooseMatch WHERE :pgs MEMBER OF gooseMatch.stats")
    Optional<GooseMatch> findMatchByPlayerGooseStats(PlayerGooseStats pgs);

    Collection<GooseMatch> findAll();
    Page<GooseMatch> findAll(Pageable pageable);



}
