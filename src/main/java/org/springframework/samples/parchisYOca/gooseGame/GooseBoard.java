package org.springframework.samples.parchisYOca.gooseGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.samples.parchisYOca.gooseGame.GooseSquare;


public class GooseBoard {
	private GooseSquare squareType;
	private Integer squareNumber;
	
	
	
	public GooseSquare getSquareByNumber(Integer boardPos) {
		Map<Integer, GooseSquare> board = getBoard();
		return board.get(boardPos);
	}
	
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
		board.put(12, GooseSquare.Bridge);
		board.put(31, GooseSquare.Well);
		board.put(19, GooseSquare.Hotel);
		board.put(52, GooseSquare.Prison);
		board.put(58, GooseSquare.Death);
		board.put(26, GooseSquare.Dice);
		board.put(53, GooseSquare.Dice);
		return board;
	}
	
	public Map<GooseSquare, List<Integer>> getBoardBySquare() {
		Map<Integer, GooseSquare> board = getBoard();
		Map<GooseSquare, List<Integer>> boardBySquare = new HashMap<GooseSquare, List<Integer>>();
		for(int i=1; i<63; ++i) {
			GooseSquare square = board.get(i);
			if(boardBySquare.containsKey(square)) {
				List<Integer> squares = boardBySquare.get(square);
				squares.add(i);
				boardBySquare.put(square, squares);
			} else {
				List<Integer> squares = new ArrayList<Integer>();
				boardBySquare.put(square, squares);
			}
		}
		return boardBySquare;
	}

}
