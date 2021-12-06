package org.springframework.samples.parchisYOca.playerGooseStats;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.player.Player;

import java.util.Collection;
import java.util.Optional;

public interface PlayerGooseStatsRepository extends CrudRepository<PlayerGooseStats, Integer> {

    @Query(value = "SELECT DISTINCT playerGooseStats FROM PlayerGooseStats playerGooseStats WHERE playerGooseStats.player.user.username = :username AND playerGooseStats.gooseMatch.id = :matchId")
    public Optional<PlayerGooseStats> findPlayerGooseStatsByUsernamedAndMatchId(String username, Integer matchId);
}
