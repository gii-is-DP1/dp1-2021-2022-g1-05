package org.springframework.samples.parchisYOca.player;


import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    @Query("SELECT player FROM Player player JOIN FETCH player.user WHERE player.user.username = :username")
    Optional<Player> findPlayerByUsername(String username) throws DataAccessException;

    @Query("SELECT player FROM Player player JOIN player.user WHERE player.user.username LIKE CONCAT('%',:username,'%')")
    Page<Player> findAllPlayersFilterByUsername(String username, Pageable pageable) throws DataAccessException;

    @Query("SELECT player FROM Player player  WHERE player.email = :email")
    Optional<Player> findPlayerByEmail(String email) throws DataAccessException;

    @Query("SELECT pgs.player FROM GooseMatch gm JOIN gm.stats pgs WHERE pgs.hasWon = 1 AND gm.matchCode = :matchCode")
    Optional<Player> findWinnerByGooseMatchCode(String matchCode) throws DataAccessException;

    @Query("SELECT pls.player FROM LudoMatch lm JOIN lm.stats pls WHERE pls.hasWon = 1 AND lm.matchCode = :matchCode")
    Optional<Player> findWinnerByLudoMatchCode(String matchCode) throws DataAccessException;

    Collection<Player> findAll();
    Page<Player> findAll(Pageable pageable);
}
