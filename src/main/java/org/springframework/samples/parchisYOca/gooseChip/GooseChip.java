package org.springframework.samples.parchisYOca.gooseChip;

import org.springframework.data.util.Pair;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class GooseChip extends BaseEntity {


    private Integer position = 0;
    private Integer inGameId = 0;
    private String color;

    @ManyToOne
    GooseBoard board;

    private static final Integer Displacement = 60;
    private static final Pair<List<Integer>, Integer> firstColum = Pair.of(Arrays.asList(0, 21, 22, 23, 24, 25, 26, 27), 0);
    private static final Pair<List<Integer>, Integer> secondColum = Pair.of(Arrays.asList(1, 20, 28, 43, 44, 45, 46, 47), 125);
    private static final Pair<List<Integer>, Integer> thirdColum = Pair.of(Arrays.asList(2, 19, 29, 42, 48, 57, 58, 59), 250);
    private static final Pair<List<Integer>, Integer> fourthColum = Pair.of(Arrays.asList(3, 18, 30, 41, 49, 55, 60, 63), 375);
    private static final Pair<List<Integer>, Integer> fifthColum = Pair.of(Arrays.asList(4, 31, 50, 61, 62, 55, 40, 17), 500);
    private static final Pair<List<Integer>, Integer> sixthColum = Pair.of(Arrays.asList(5, 32, 51, 52, 53, 54, 39, 16), 625);
    private static final Pair<List<Integer>, Integer> seventhColum = Pair.of(Arrays.asList(6, 33, 34, 35, 36, 37, 38, 15), 750);
    private static final Pair<List<Integer>, Integer> eigthColum = Pair.of(Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14), 875);
    private static final Pair<List<Integer>, Integer> firstRow = Pair.of(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7), 0);
    private static final Pair<List<Integer>, Integer> secondRow = Pair.of(Arrays.asList(8, 27, 28, 29, 30, 31, 32, 33), 125);
    private static final Pair<List<Integer>, Integer> thirdRow = Pair.of(Arrays.asList(26, 47, 48, 49, 50 ,51, 34, 9), 250);
    private static final Pair<List<Integer>, Integer> fourthRow = Pair.of(Arrays.asList(25, 46, 59, 60, 61,52, 35, 10), 375);
    private static final Pair<List<Integer>, Integer> fifthRow = Pair.of(Arrays.asList(24, 45, 58, 63, 62, 53, 36, 11), 500);
    private static final Pair<List<Integer>, Integer> sixthRow = Pair.of(Arrays.asList(23, 44, 57, 56, 55, 54, 37, 12), 625);
    private static final Pair<List<Integer>, Integer> seventhRow = Pair.of(Arrays.asList(13, 22, 38, 39, 40, 41, 42, 43), 750);
    private static final Pair<List<Integer>, Integer> eigthRow = Pair.of(Arrays.asList(14, 15, 16, 17, 18, 19, 20, 21), 875);

    public String getColor() {
    	Color[] colores = Color.values();
    	String color = colores[inGameId].toString();
    	return color;
    }


    public static Integer getPositionXInPixels(Integer position, Integer inGameId) {
    	Integer positionX = null;
    	if(firstColum.getFirst().contains(position)) {positionX = firstColum.getSecond();}
    	if(secondColum.getFirst().contains(position)) {positionX = secondColum.getSecond();}
    	if(thirdColum.getFirst().contains(position)) {positionX = thirdColum.getSecond();}
    	if(fourthColum.getFirst().contains(position)) {positionX = fourthColum.getSecond();}
    	if(fifthColum.getFirst().contains(position)) {positionX = fifthColum.getSecond();}
    	if(sixthColum.getFirst().contains(position)) {positionX = sixthColum.getSecond();}
    	if(seventhColum.getFirst().contains(position)) {positionX = seventhColum.getSecond();}
    	if(eigthColum.getFirst().contains(position)) {positionX = eigthColum.getSecond();}
    	Integer displacement = getDisplacenmentX(inGameId);

    	return positionX + displacement;
    }
    public static Integer getPositionYInPixels(Integer position, Integer inGameId) {
    	Integer positionY = null;
    	if(firstRow.getFirst().contains(position)) {positionY = firstRow.getSecond();}
    	if(secondRow.getFirst().contains(position)) {positionY = secondRow.getSecond();}
    	if(thirdRow.getFirst().contains(position)) {positionY = thirdRow.getSecond();}
    	if(fourthRow.getFirst().contains(position)) {positionY = fourthRow.getSecond();}
    	if(fifthRow.getFirst().contains(position)) {positionY = fifthRow.getSecond();}
    	if(sixthRow.getFirst().contains(position)) {positionY = sixthRow.getSecond();}
    	if(seventhRow.getFirst().contains(position)) {positionY = seventhRow.getSecond();}
    	if(eigthRow.getFirst().contains(position)) {positionY = eigthRow.getSecond();}
    	Integer displacement = getDisplacenmentY(inGameId);

    	return positionY + displacement;
    }

    public static Integer getDisplacenmentX(Integer inGameId) {
    	Integer displacement = 0;
    	switch(inGameId) {
    	case 1:
    		displacement = Displacement;
    		break;
    	case 3:
    		displacement = Displacement;
    		break;
    	}
    	return displacement;
    }
    public static Integer getDisplacenmentY(Integer inGameId) {
    	Integer displacement = 0;
    	switch(inGameId) {
    	case 2:
    		displacement = Displacement;
    		break;
    	case 3:
    		displacement = Displacement;
    		break;
    	}
    	return displacement;
    }
}
