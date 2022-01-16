package org.springframework.samples.parchisYOca.ludoChip;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface LudoChipRepository extends CrudRepository<LudoChip,Integer> {

    @Query(value = "SELECT DISTINCT ludoChip FROM LudoChip ludoChip JOIN FETCH ludoChip.board board WHERE board.match.id = :matchId")
    Collection<LudoChip> findChipsByMatchId(Integer matchId);

    @Query(value = "SELECT DISTINCT ludoChip FROM LudoChip ludoChip JOIN FETCH ludoChip.board board WHERE board.match.id = :matchId" +
        " AND ludoChip.inGameChipId = :inGameChipId AND ludoChip.inGamePlayerId = :inGamePlayerId")
    Optional<LudoChip> findChip(Integer matchId, Integer inGameChipId, Integer inGamePlayerId);


}
