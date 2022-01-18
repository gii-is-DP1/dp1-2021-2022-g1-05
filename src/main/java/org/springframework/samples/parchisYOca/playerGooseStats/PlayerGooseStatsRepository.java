package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface PlayerGooseStatsRepository extends CrudRepository<PlayerGooseStats, Integer> {

    @Query(value = "SELECT DISTINCT playerGooseStats FROM GooseMatch gm JOIN gm.stats playerGooseStats WHERE playerGooseStats.player.user.username = :username AND gm.id = :matchId")
    Optional<PlayerGooseStats> findPlayerGooseStatsByUsernamedAndMatchId(String username, Integer matchId);

    @Query(value = "SELECT DISTINCT playerGooseStats FROM GooseMatch gm JOIN gm.stats playerGooseStats WHERE playerGooseStats.inGameId = :inGameId AND gm.id = :matchId")
    Optional<PlayerGooseStats> findPlayerGooseStatsByInGameIdAndMatchId(Integer inGameId, Integer matchId);

    @Query(value = "SELECT DISTINCT playerGooseStats FROM PlayerGooseStats playerGooseStats WHERE playerGooseStats.player.user.username = :username")
    Collection<PlayerGooseStats> findPlayerGooseStatsByUsername(String username);

}
