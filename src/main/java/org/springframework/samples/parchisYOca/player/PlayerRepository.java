package org.springframework.samples.parchisYOca.player;


import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.petclinic.owner.Owner;

import java.util.Collection;

public interface PlayerRepository extends CrudRepository<Player, Integer> {

}
