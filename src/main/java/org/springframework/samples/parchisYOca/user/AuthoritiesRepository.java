package org.springframework.samples.parchisYOca.user;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.player.Player;

import java.util.Optional;


public interface AuthoritiesRepository extends  CrudRepository<Authorities, Integer>{

}
