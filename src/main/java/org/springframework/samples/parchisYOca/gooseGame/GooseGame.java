package org.springframework.samples.parchisYOca.gooseGame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.samples.parchisYOca.gooseGame.GooseChip;
import org.springframework.samples.parchisYOca.player.Player;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GooseGame {
	private List<Player> players;
	private Map<Integer, GooseSquare> board;
	private Integer dice;
	private List<GooseChip> chips;
	
	public Map<Integer, GooseSquare> getBoard() {
		Map<Integer, GooseSquare> board = new HashMap<Integer, GooseSquare>();
		for(int i=1; i<64; ++i) {
			
			board.put(i, GooseSquare.Common);
		}
		board.put(1, GooseSquare.Start);
		board.put(63, GooseSquare.Finnish);
		List<Integer> geese = Arrays.asList(5, 9, 14, 18, 23, 27, 32, 36, 41, 45, 50, 54, 59);
		for(Integer goose:geese) {
			board.put(goose, GooseSquare.Goose);
		}
		board.put(40, GooseSquare.Maze);
		board.put(6, GooseSquare.Bridge);
		board.put(12, GooseSquare.Bridge);
		board.put(31, GooseSquare.Well);
		board.put(19, GooseSquare.Hotel);
		board.put(52, GooseSquare.Prison);
		board.put(58, GooseSquare.Death);
		board.put(26, GooseSquare.Dice);
		board.put(53, GooseSquare.Dice);
		return board;
	}
	public Integer getDice() {
		Integer rnd = new Random().ints(1,1,6).findFirst().getAsInt();
		return rnd;
	}

}
