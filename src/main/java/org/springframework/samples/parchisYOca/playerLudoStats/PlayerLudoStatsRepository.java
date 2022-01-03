package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;

import java.util.Collection;
import java.util.Optional;

public interface PlayerLudoStatsRepository extends CrudRepository<PlayerLudoStats, Integer> {

    @Query(value = "SELECT DISTINCT playerLudoStats FROM PlayerLudoStats playerLudoStats WHERE playerLudoStats.player.user.username = :username AND playerLudoStats.ludoMatch.id = :matchId")
    Optional<PlayerLudoStats> findPlayerLudoStatsByUsernameAndMatchId(String username, Integer matchId);

    @Query(value = "SELECT DISTINCT playerLudoStats FROM PlayerLudoStats playerLudoStats WHERE playerLudoStats.inGameId = :inGameId AND playerLudoStats.ludoMatch.id = :matchId")
    Optional<PlayerLudoStats> findPlayerLudoStatsByInGameIdAndMatchId(Integer inGameId, Integer matchId);

    @Modifying
    @Query(value = "DELETE FROM PlayerLudoStats playerLudoStats WHERE playerLudoStats.ludoMatch.id = :matchId And playerLudoStats.inGameId = :statsId")
    void deletePlayerFromGame(Integer statsId, Integer matchId);

    @Modifying
    @Query(value = "DELETE FROM PlayerLudoStats playerLudoStats WHERE playerLudoStats.ludoMatch.id = :matchId")
    void deleteStatsFromGame(Integer matchId);
}
