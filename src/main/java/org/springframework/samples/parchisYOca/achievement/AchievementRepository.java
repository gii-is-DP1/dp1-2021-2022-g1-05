package org.springframework.samples.parchisYOca.achievement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface AchievementRepository extends  CrudRepository<Achievement, Integer>{

    Page<Achievement> findAll(Pageable pageable);
    Collection<Achievement> findAll();

}
