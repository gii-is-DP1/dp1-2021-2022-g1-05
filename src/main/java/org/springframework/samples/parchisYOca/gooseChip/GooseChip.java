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
    	pixelBoard.put(0, Pair.of(57, 585)); pixelBoard.put(1, Pair.of(229,584));
    	pixelBoard.put(2, Pair.of(295,584)); pixelBoard.put(3, Pair.of(353, 584));
    	pixelBoard.put(4, Pair.of(413, 584)); pixelBoard.put(5, Pair.of(473, 584));
    	pixelBoard.put(6, Pair.of(543, 584)); pixelBoard.put(7, Pair.of(616, 584));
    	pixelBoard.put(8, Pair.of(669, 584)); pixelBoard.put(9, Pair.of(713, 573));
    	pixelBoard.put(10, Pair.of(810, 502)); pixelBoard.put(11, Pair.of(839, 459));
    	pixelBoard.put(12, Pair.of(855, 413)); pixelBoard.put(13, Pair.of(863, 362));
    	pixelBoard.put(14, Pair.of(863, 296)); pixelBoard.put(15, Pair.of(846, 240));
    	pixelBoard.put(16, Pair.of(891, 154)); pixelBoard.put(17, Pair.of(851, 102));
    	pixelBoard.put(18, Pair.of(751, 33)); pixelBoard.put(19, Pair.of(656, 16));
    	pixelBoard.put(20, Pair.of(588, 16)); pixelBoard.put(21, Pair.of(532, 16));
    	pixelBoard.put(22, Pair.of(473, 16)); pixelBoard.put(23, Pair.of(404, 16));
    	pixelBoard.put(24, Pair.of(347, 16)); pixelBoard.put(25, Pair.of(289, 16));
    	pixelBoard.put(26, Pair.of(211, 23)); pixelBoard.put(27, Pair.of(141, 56));
    	pixelBoard.put(28, Pair.of(95, 92)); pixelBoard.put(29, Pair.of(67, 134));
    	pixelBoard.put(30, Pair.of(40, 182)); pixelBoard.put(31, Pair.of(30, 228));
    	pixelBoard.put(32, Pair.of(22, 305)); pixelBoard.put(33, Pair.of(44, 381));
    	pixelBoard.put(34, Pair.of(66, 439)); pixelBoard.put(35, Pair.of(166, 435));
    	pixelBoard.put(36, Pair.of(200, 465)); pixelBoard.put(37, Pair.of(249, 485));
    	pixelBoard.put(38, Pair.of(292, 489)); pixelBoard.put(39, Pair.of(352, 489));
    	return posToPixelMapCon(pixelBoard);
    }
    public static Map<Integer, Pair<Integer, Integer>> posToPixelMapCon( 
    		Map<Integer, Pair<Integer, Integer>> pixelBoard) {
    	//Indexes 40 -> 63
    	pixelBoard.put(40, Pair.of(412, 489)); pixelBoard.put(41, Pair.of(472, 489));
    	pixelBoard.put(42, Pair.of(540, 489)); pixelBoard.put(43, Pair.of(615, 489));
    	pixelBoard.put(44, Pair.of(663, 489)); pixelBoard.put(45, Pair.of(703, 466));
    	pixelBoard.put(46, Pair.of(753, 391)); pixelBoard.put(47, Pair.of(764, 342));
    	pixelBoard.put(48, Pair.of(759, 299)); pixelBoard.put(49, Pair.of(755, 244));
    	pixelBoard.put(50, Pair.of(732, 130)); pixelBoard.put(51, Pair.of(664, 110));
    	pixelBoard.put(52, Pair.of(553, 110)); pixelBoard.put(53, Pair.of(483, 110));
    	pixelBoard.put(54, Pair.of(405, 110)); pixelBoard.put(55, Pair.of(346, 110));
    	pixelBoard.put(56, Pair.of(277, 110)); pixelBoard.put(57, Pair.of(212, 129));
    	pixelBoard.put(58, Pair.of(160, 170)); pixelBoard.put(59, Pair.of(126, 257));
    	pixelBoard.put(60, Pair.of(144, 352)); pixelBoard.put(61, Pair.of(209, 375));
    	pixelBoard.put(62, Pair.of(261, 383)); pixelBoard.put(63, Pair.of(355, 255));
    	
    	return pixelBoard;
    }
    public static Integer getPositionXInPixels(Integer position) {
    	return posToPixelMap().get(position).getFirst();
    }
    public static Integer getPositionYInPixels(Integer position) {
    	return posToPixelMap().get(position).getSecond();
    }

}
