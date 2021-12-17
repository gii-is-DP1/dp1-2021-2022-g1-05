package org.springframework.samples.parchisYOca.gooseChip;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.util.Pair;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class GooseChip extends BaseEntity {


    private Integer position = 0;
    private Integer inGameId = 0;

    //TODO tenemos que poner playerId en ludo

    @ManyToOne
    GooseBoard board;

    public static Map<Integer, Pair<Integer, Integer>> posToPixelMap() {
    	//Indexes 0 -> 39
    	Map<Integer, Pair<Integer, Integer>> pixelBoard =
    			new HashMap<Integer, Pair<Integer, Integer>>();
    	pixelBoard.put(0, Pair.of(0, 0)); pixelBoard.put(1, Pair.of(125,0));
    	pixelBoard.put(2, Pair.of(250,0)); pixelBoard.put(3, Pair.of(375, 0));
    	pixelBoard.put(4, Pair.of(500, 0)); pixelBoard.put(5, Pair.of(625, 0));
    	pixelBoard.put(6, Pair.of(750, 0)); pixelBoard.put(7, Pair.of(875, 0));
    	pixelBoard.put(8, Pair.of(875, 125)); pixelBoard.put(9, Pair.of(875, 250));
    	pixelBoard.put(10, Pair.of(875, 375)); pixelBoard.put(11, Pair.of(875, 500));
    	pixelBoard.put(12, Pair.of(875, 625)); pixelBoard.put(13, Pair.of(875, 750));
    	pixelBoard.put(14, Pair.of(875, 875)); pixelBoard.put(15, Pair.of(750, 875));
    	pixelBoard.put(16, Pair.of(625, 875)); pixelBoard.put(17, Pair.of(500, 875));
    	pixelBoard.put(18, Pair.of(375, 875)); pixelBoard.put(19, Pair.of(250, 875));
    	pixelBoard.put(20, Pair.of(125, 875)); pixelBoard.put(21, Pair.of(0, 875));
    	pixelBoard.put(22, Pair.of(0, 750)); pixelBoard.put(23, Pair.of(0, 625));
    	pixelBoard.put(24, Pair.of(0, 500)); pixelBoard.put(25, Pair.of(0, 375));
    	pixelBoard.put(26, Pair.of(0, 250)); pixelBoard.put(27, Pair.of(0, 125));
    	pixelBoard.put(28, Pair.of(125, 125)); pixelBoard.put(29, Pair.of(250, 125));
    	pixelBoard.put(30, Pair.of(375, 125)); pixelBoard.put(31, Pair.of(500, 125));
    	pixelBoard.put(32, Pair.of(625, 125)); pixelBoard.put(33, Pair.of(750, 125));
    	pixelBoard.put(34, Pair.of(750, 250)); pixelBoard.put(35, Pair.of(750, 375));
    	pixelBoard.put(36, Pair.of(750, 500)); pixelBoard.put(37, Pair.of(750, 625));
    	pixelBoard.put(38, Pair.of(750, 750)); pixelBoard.put(39, Pair.of(625, 750));
    	return posToPixelMapCon(pixelBoard);
    }
    public static Map<Integer, Pair<Integer, Integer>> posToPixelMapCon( 
    		Map<Integer, Pair<Integer, Integer>> pixelBoard) {
    	//Indexes 40 -> 63
    	pixelBoard.put(40, Pair.of(500, 750)); pixelBoard.put(41, Pair.of(375, 750));
    	pixelBoard.put(42, Pair.of(250, 750)); pixelBoard.put(43, Pair.of(125, 750));
    	pixelBoard.put(44, Pair.of(125, 625)); pixelBoard.put(45, Pair.of(125, 500));
    	pixelBoard.put(46, Pair.of(125, 375)); pixelBoard.put(47, Pair.of(125, 250));
    	pixelBoard.put(48, Pair.of(250, 250)); pixelBoard.put(49, Pair.of(375, 250));
    	pixelBoard.put(50, Pair.of(500, 250)); pixelBoard.put(51, Pair.of(625, 250));
    	pixelBoard.put(52, Pair.of(625, 375)); pixelBoard.put(53, Pair.of(625, 500));
    	pixelBoard.put(54, Pair.of(625, 625)); pixelBoard.put(55, Pair.of(500, 625));
    	pixelBoard.put(56, Pair.of(375, 625)); pixelBoard.put(57, Pair.of(250, 625));
    	pixelBoard.put(58, Pair.of(250, 500)); pixelBoard.put(59, Pair.of(250, 375));
    	pixelBoard.put(60, Pair.of(375, 375)); pixelBoard.put(61, Pair.of(500, 375));
    	pixelBoard.put(62, Pair.of(500, 500)); pixelBoard.put(63, Pair.of(375, 500));
    	
    	return pixelBoard;
    }
    public static Integer getPositionXInPixels(Integer position, Integer inGameId) {
    	Integer xPosition =  posToPixelMap().get(position).getFirst();
    	Integer displacement = getDisplacenmentX(inGameId);
    	
    	return xPosition + displacement;
    }
    public static Integer getPositionYInPixels(Integer position, Integer inGameId) {
    	Integer yPosition =  posToPixelMap().get(position).getSecond();
    	Integer displacement = getDisplacenmentY(inGameId);
    	
    	return yPosition + displacement;
    }
    
    public static Integer getDisplacenmentX(Integer inGameId) {
    	Integer displacement = 0;
    	switch(inGameId) {
    	case 0:
    		break;
    	case 1:
    		displacement = 60;
    		break;
    	case 2:
    		break;
    	case 3:
    		displacement = 60;
    		break;
    	}
    	return displacement;
    }
    public static Integer getDisplacenmentY(Integer inGameId) {
    	Integer displacement = 0;
    	switch(inGameId) {
    	case 0:
    		break;
    	case 1:
    		break;
    	case 2:
    		displacement = 60;
    		break;
    	case 3:
    		displacement = 60;
    		break;
    	}
    	return displacement;
    }
}
