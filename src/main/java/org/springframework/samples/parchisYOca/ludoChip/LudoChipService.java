package org.springframework.samples.parchisYOca.ludoChip;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchRepository;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsRepository;

public class LudoChipService {
	private static final Integer RED_LAST_TILE = 33; //uno menos que en el tablero porque
	private static final Integer YELLOW_LAST_TILE = 67;//internamente vamos de 0-67
	private static final Integer GREEN_LAST_TILE = 50;
	private static final Integer BLUE_LAST_TILE = 16;
	private static final List<Integer> SAVE_TILES = Arrays.asList(4,11,16,21,28,33,38,45,50,55,62,67);
	
	@Autowired
	private LudoChipRepository ludoChipRepo;
	private PlayerLudoStatsRepository playerRepo;
	private LudoMatchRepository ludoMatchRepo;
	
	public LudoChipService(LudoChipRepository ludoChipRepo,PlayerLudoStatsRepository playerRepo,
			LudoMatchRepository ludoMatchRepo) {
		this.ludoChipRepo = ludoChipRepo;
		this.ludoMatchRepo = ludoMatchRepo;
		this.playerRepo = playerRepo;
	}
}
