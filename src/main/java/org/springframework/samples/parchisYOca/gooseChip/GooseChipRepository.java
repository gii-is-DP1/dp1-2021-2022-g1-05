package org.springframework.samples.parchisYOca.gooseChip;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface GooseChipRepository extends CrudRepository<GooseChip, Integer> {

    @Query(value = "SELECT DISTINCT gooseChip FROM GooseChip gooseChip JOIN FETCH gooseChip.board board WHERE board.match.id = :matchId")
    Collection<GooseChip> findChipsByMatchId(Integer matchId);
}
