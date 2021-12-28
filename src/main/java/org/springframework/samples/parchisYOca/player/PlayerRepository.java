package org.springframework.samples.parchisYOca.player;


import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

    @Query("SELECT player FROM Player player JOIN FETCH player.user WHERE player.user.username = :username")
    Optional<Player> findPlayerByUsername(String username) throws DataAccessException;

    @Query("SELECT player FROM Player player  WHERE player.email = :email")
    Optional<Player> findPlayerByEmail(String email) throws DataAccessException;
}
