package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;

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

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch JOIN ludoMatch.stats stats WHERE stats.player.user.username = :username")
    Page<LudoMatch> findMatchesByUsernameWithPaging(String username, Pageable pageable);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.startDate > :date")
    Page<LudoMatch> findLudoMatchByStartDate(Date date, Pageable pageable);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.endDate > :date")
    Page<LudoMatch> findLudoMatchByEndDate(Date date, Pageable pageable);

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE ludoMatch.endDate is not null")
    Collection<LudoMatch> findEndedLudoMatches();

    @Query(value = "SELECT DISTINCT ludoMatch FROM LudoMatch ludoMatch WHERE :pls MEMBER OF ludoMatch.stats")
    Optional<LudoMatch> findMatchByPlayerLudoStats(PlayerLudoStats pls);

    Collection<LudoMatch> findAll();
    Page<LudoMatch> findAll(Pageable pageable);
}
