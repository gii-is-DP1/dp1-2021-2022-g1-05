package org.springframework.samples.parchisYOca.ludoChip;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface LudoChipRepository extends CrudRepository<LudoChip,Integer> {

    @Query(value = "SELECT DISTINCT ludoChip FROM LudoChip ludoChip JOIN FETCH ludoChip.board board WHERE board.match.id = :matchId")
    Collection<LudoChip> findChipsByMatchId(Integer matchId);

}
