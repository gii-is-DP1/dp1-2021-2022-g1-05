package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;

import java.util.Optional;

public interface PlayerLudoStatsRepository extends CrudRepository<PlayerLudoStats, Integer> {

    @Query(value = "SELECT DISTINCT playerLudoStats FROM PlayerLudoStats playerLudoStats WHERE playerLudoStats.player.user.username = :username AND playerLudoStats.ludoMatch.id = :matchId")
    public Optional<PlayerLudoStats> findPlayerLudoStatsByUsernameAndMatchId(String username, Integer matchId);
}
