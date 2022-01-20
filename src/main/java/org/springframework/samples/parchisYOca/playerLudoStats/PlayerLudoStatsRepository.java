package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;

import java.util.Collection;
import java.util.Optional;

public interface PlayerLudoStatsRepository extends CrudRepository<PlayerLudoStats, Integer> {

    @Query(value = "SELECT DISTINCT playerLudoStats FROM LudoMatch lm JOIN lm.stats playerLudoStats WHERE playerLudoStats.player.user.username = :username AND lm.id = :matchId")
    Optional<PlayerLudoStats> findPlayerLudoStatsByUsernameAndMatchId(String username, Integer matchId);

    @Query(value = "SELECT DISTINCT playerLudoStats FROM LudoMatch lm JOIN lm.stats playerLudoStats WHERE playerLudoStats.inGameId = :inGameId AND lm.id = :matchId")
    Optional<PlayerLudoStats> findPlayerLudoStatsByInGameIdAndMatchId(Integer inGameId, Integer matchId);

    @Query(value = "SELECT DISTINCT playerLudoStats FROM PlayerLudoStats playerLudoStats WHERE playerLudoStats.player.user.username = :username")
    Collection<PlayerLudoStats> findPlayerLudoStatsByUsername(String username);

    @Query(value = "SELECT DISTINCT stats FROM LudoMatch ludoMatch JOIN ludoMatch.stats stats WHERE ludoMatch.id = :id")
    Collection<PlayerLudoStats> findPlayerLudoStatsByGameId(Integer id);

    Collection<PlayerLudoStats> findAll();
}
