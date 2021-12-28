package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.player.Player;

import java.util.Collection;
import java.util.Optional;

public interface PlayerGooseStatsRepository extends CrudRepository<PlayerGooseStats, Integer> {

    @Query(value = "SELECT DISTINCT playerGooseStats FROM PlayerGooseStats playerGooseStats WHERE playerGooseStats.player.user.username = :username AND playerGooseStats.gooseMatch.id = :matchId")
    Optional<PlayerGooseStats> findPlayerGooseStatsByUsernamedAndMatchId(String username, Integer matchId);

    @Query(value = "SELECT DISTINCT playerGooseStats FROM PlayerGooseStats playerGooseStats WHERE playerGooseStats.inGameId = :inGameId AND playerGooseStats.gooseMatch.id = :matchId")
    Optional<PlayerGooseStats> findPlayerGooseStatsByInGameIdAndMatchId(Integer inGameId, Integer matchId);

    @Modifying
    @Query(value = "DELETE FROM PlayerGooseStats playerGooseStats WHERE playerGooseStats.gooseMatch.id = :matchId And playerGooseStats.inGameId = :statsId")
    void deletePlayerFromGame(Integer statsId, Integer matchId);

    @Modifying
    @Query(value = "DELETE FROM PlayerGooseStats playerGooseStats WHERE playerGooseStats.gooseMatch.id = :matchId")
    void deleteStatsFromGame(Integer matchId);
}
