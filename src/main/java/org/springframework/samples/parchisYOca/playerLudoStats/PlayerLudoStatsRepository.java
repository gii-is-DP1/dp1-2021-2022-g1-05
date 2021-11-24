package org.springframework.samples.parchisYOca.playerLudoStats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;

public interface PlayerLudoStatsRepository extends CrudRepository<PlayerLudoStats, Integer> {
}
