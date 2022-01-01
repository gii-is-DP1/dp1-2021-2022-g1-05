package org.springframework.samples.parchisYOca.ludoChip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchRepository;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;



@Service
public class LudoChipService {
	private static final Integer RED_LAST_TILE = 33; //uno menos que en el tablero porque
	private static final Integer YELLOW_LAST_TILE = 67;//internamente vamos de 0-67
	private static final Integer GREEN_LAST_TILE = 50;
	private static final Integer BLUE_LAST_TILE = 16;
	private static final List<Integer> SAVE_TILES = Arrays.asList(4,11,16,21,28,33,38,45,50,55,62,67);


	private LudoChipRepository ludoChipRepository;
	private LudoMatchRepository ludoMatchRepository;
    private PlayerLudoStatsRepository playerLudoStatsRepository;


    @Autowired
	public LudoChipService(LudoChipRepository ludoChipRepository,PlayerLudoStatsRepository playerLudoStatsRepository,
			LudoMatchRepository ludoMatchRepository) {
		this.ludoChipRepository = ludoChipRepository;
		this.ludoMatchRepository = ludoMatchRepository;
        this.playerLudoStatsRepository=playerLudoStatsRepository;

	}
    @Transactional
    public Collection<LudoChip> findChipsByMatchId(Integer matchId) throws DataAccessException {
        return ludoChipRepository.findChipsByMatchId(matchId);
    }

    @Transactional
    public LudoChip save(LudoChip ludoChip) {
        return ludoChipRepository.save(ludoChip);
    }



}
