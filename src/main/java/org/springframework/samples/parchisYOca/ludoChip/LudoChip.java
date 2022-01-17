package org.springframework.samples.parchisYOca.ludoChip;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.samples.parchisYOca.util.Color;
import org.springframework.data.util.Pair;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class LudoChip  extends BaseEntity {
	private static final Integer NARROW_TILE = 42;
	private static final Integer WIDE_TILE = 90;
	private static final Integer BORDER_WIDTH=14;
	private static final Integer HOME_DISPLACEMENT=170;
	private static final Integer X_HOME_L = 53;
	private static final Integer X_HOME_R= 633;
	private static final Integer Y_HOME_T = 53;
	private static final Integer Y_HOME_B = 633;
	private static final Pair<List<Integer>, Integer> row1 = Pair.of(Arrays.asList(32, 33, 34), 14);
	private static final Pair<List<Integer>, Integer> row2 = Pair.of(Arrays.asList(31, 35), 53);
	private static final Pair<List<Integer>, Integer> row3 = Pair.of(Arrays.asList(30, 36), 95);
	private static final Pair<List<Integer>, Integer> row4 = Pair.of(Arrays.asList(29, 37), 140);
	private static final Pair<List<Integer>, Integer> row5 = Pair.of(Arrays.asList(28, 38), 180);
	private static final Pair<List<Integer>, Integer> row6 = Pair.of(Arrays.asList(27, 39), 223);
	private static final Pair<List<Integer>, Integer> row7 = Pair.of(Arrays.asList(26, 40), 266);
	private static final Pair<List<Integer>, Integer> row8 = Pair.of(Arrays.asList(17,18,19,20,21,22,23,43,44,
			45,46,47,48,49,25,41), 304);//Esquinas 7,8,24,25,41,42,58,59
	private static final Pair<List<Integer>, Integer> row9 = Pair.of(Arrays.asList(16, 50), 400);
	private static final Pair<List<Integer>, Integer> row10 = Pair.of(Arrays.asList(9,10,11,12,13,14,15,51,52,
			53,54,55,56,57,58,8), 500);
	private static final Pair<List<Integer>, Integer> row11 = Pair.of(Arrays.asList(60, 6), 596);
	private static final Pair<List<Integer>, Integer> row12 = Pair.of(Arrays.asList(61, 5), 634);
	private static final Pair<List<Integer>, Integer> row13 = Pair.of(Arrays.asList(62, 4), 677);
	private static final Pair<List<Integer>, Integer> row14 = Pair.of(Arrays.asList(63, 3), 720);
	private static final Pair<List<Integer>, Integer> row15 = Pair.of(Arrays.asList(64, 2), 762);
	private static final Pair<List<Integer>, Integer> row16 = Pair.of(Arrays.asList(65, 1), 804);
	private static final Pair<List<Integer>, Integer> row17 = Pair.of(Arrays.asList(66, 67, 0), 847);
	private static final Pair<List<Integer>, Integer> col1 = Pair.of(Arrays.asList(49,50,51), 14);
	private static final Pair<List<Integer>, Integer> col2 = Pair.of(Arrays.asList(48,52), 53);
	private static final Pair<List<Integer>, Integer> col3 = Pair.of(Arrays.asList(47,53), 95);
	private static final Pair<List<Integer>, Integer> col4 = Pair.of(Arrays.asList(46,54), 140);
	private static final Pair<List<Integer>, Integer> col5 = Pair.of(Arrays.asList(45,55), 180);
	private static final Pair<List<Integer>, Integer> col6 = Pair.of(Arrays.asList(44,56), 223);
	private static final Pair<List<Integer>, Integer> col7 = Pair.of(Arrays.asList(43,57), 266);
	private static final Pair<List<Integer>, Integer> col8 = Pair.of(Arrays.asList(34,35,36,37,38,39,40,60,61,
			62,63,64,65,66,42,58), 304);
	private static final Pair<List<Integer>, Integer> col9 = Pair.of(Arrays.asList(33,67), 400);
	private static final Pair<List<Integer>, Integer> col10 = Pair.of(Arrays.asList(0,1,2,3,4,5,6,26,27,28,29,
			30,31,32,7,25), 500);
	private static final Pair<List<Integer>, Integer> col11 = Pair.of(Arrays.asList(9,23), 596);
	private static final Pair<List<Integer>, Integer> col12 = Pair.of(Arrays.asList(10,22), 634);
	private static final Pair<List<Integer>, Integer> col13 = Pair.of(Arrays.asList(11, 21), 677);
	private static final Pair<List<Integer>, Integer> col14 = Pair.of(Arrays.asList(12,20), 720);
	private static final Pair<List<Integer>, Integer> col15 = Pair.of(Arrays.asList(13,19), 762);
	private static final Pair<List<Integer>, Integer> col16 = Pair.of(Arrays.asList(14,18), 804);
	private static final Pair<List<Integer>, Integer> col17 = Pair.of(Arrays.asList(15,16,17), 847);
	private static final Pair<List<Integer>, Integer> corsY2442 = Pair.of(Arrays.asList(24,42), 334);
	private static final Pair<List<Integer>, Integer> corsY597 = Pair.of(Arrays.asList(59,7), 546);
	private static final Pair<List<Integer>, Integer> corsX4159 = Pair.of(Arrays.asList(41,59), 334);
	private static final Pair<List<Integer>, Integer> corsX824 = Pair.of(Arrays.asList(8,24), 546);


	private Integer position;
    private Color color;
    @Range(min=0, max=3)
    private Integer inGamePlayerId;
    //Para diferenciar las distintas fichas del mismo color
    @Range(min=0, max=3)
    private Integer inGameChipId;

    //El juego depende mucho de en que parte del tablero se encuentra la ficha
    //earlyGame -> casilla de inicio -> sacar 5 para pasar al sguiente estado
    //midGame -> juego normal del parchis
    //endGame -> las casillas antes de llegar a la meta
    private GameState gameState = GameState.earlyGame;

    @ManyToOne
    LudoBoard board;

    public Color getColor() {
        if(this.color==null){
            setColor();
        }
    	return this.color;

    }
    public void setColor(){
        Color[] colores = Color.values();
        this.color = colores[inGamePlayerId];
    }

    public void setPosition(Integer position) {

    	switch(gameState) {
    	case earlyGame:
    		this.position = null; //Está en casa, la posición es irrelevante
    		break;
    	case midGame:
    		this.position = position%68;
    		//el tablero es circular pero cada ficha empieza desde un punto distinto
    		break;
    	case endGame:
    		//ver como posicionar la ficha en esta parte del juego
    		this.position = position;
    	}

    }
    public static Integer getX(Color color,Integer chipId,GameState gameState,
    		Boolean isNotAlone, Integer position) {
    	Integer x = null;
    	switch(gameState) {
    	case earlyGame:
    		x = getXHome(color, chipId);
    		break;
    	case midGame:
    		x = getXMidgame(position, isNotAlone);
    		break;
    	case endGame:
    		break;
    	}
    	return x;
    }
    public static Integer getY(Color color,Integer chipId,GameState gameState,
    		Boolean isNotAlone, Integer position) {
    	Integer y = null;
    	switch(gameState) {
    	case earlyGame:
    		y = getYHome(color, chipId);
    		break;
    	case midGame:
    		y = getYMidgame(position, isNotAlone);
    		break;
    	case endGame:
		break;
	}
    	return y;
    }
    public static Integer getXHome(Color color, Integer chipId) {
    	Integer homeX = null;
    	Integer disp = null;
    	switch(color) {
    	case Red:
    		homeX = X_HOME_L;
    		break;
    	case Green:
    		homeX = X_HOME_L;
    		break;
    	case Blue:
    		homeX = X_HOME_R;
    		break;
    	case Yellow:
    		homeX = X_HOME_R;
    		break;
    	}
    	switch(chipId) {
    	case 1:
    		disp = HOME_DISPLACEMENT;
    		break;
    	case 3:
    		disp = HOME_DISPLACEMENT;
    		break;
    	default:
    		disp = 0;
    	}
    	return homeX + disp;
    }
    public static Integer getYHome(Color color, Integer chipId) {
    	Integer homeY = null;
    	Integer disp = null;
    	switch(color) {
    	case Red:
    		homeY = Y_HOME_T;
    		break;
    	case Green:
    		homeY = Y_HOME_B;
    		break;
    	case Blue:
    		homeY = Y_HOME_T;
    		break;
    	case Yellow:
    		homeY = Y_HOME_B;
    		break;
    	}
    	switch(chipId) {
    	case 2:
    		disp= HOME_DISPLACEMENT;
    		break;
    	case 3:
    		disp = HOME_DISPLACEMENT;
    		break;
    	default:
    		disp = 0;
    	}
    	return homeY + disp;
    }
    public static Integer getXMidgame(Integer position, Boolean isNotAlone) {
    	Integer x = null;
    	if(col1.getFirst().contains(position)) {x = col1.getSecond();}
    	if(col2.getFirst().contains(position)) {x = col2.getSecond();}
    	if(col3.getFirst().contains(position)) {x = col3.getSecond();}
    	if(col4.getFirst().contains(position)) {x = col4.getSecond();}
    	if(col5.getFirst().contains(position)) {x = col5.getSecond();}
    	if(col6.getFirst().contains(position)) {x = col6.getSecond();}
    	if(col7.getFirst().contains(position)) {x = col7.getSecond();}
    	if(col8.getFirst().contains(position)) {x = col8.getSecond();}
    	if(col9.getFirst().contains(position)) {x = col9.getSecond();}
    	if(col10.getFirst().contains(position)) {x = col10.getSecond();}
    	if(col11.getFirst().contains(position)) {x = col11.getSecond();}
    	if(col12.getFirst().contains(position)) {x = col12.getSecond();}
    	if(col13.getFirst().contains(position)) {x = col13.getSecond();}
    	if(col14.getFirst().contains(position)) {x = col14.getSecond();}
    	if(col15.getFirst().contains(position)) {x = col15.getSecond();}
    	if(col16.getFirst().contains(position)) {x = col16.getSecond();}
    	if(col17.getFirst().contains(position)) {x = col17.getSecond();}
    	if(corsX4159.getFirst().contains(position)) {x = corsX4159.getSecond();}
    	if(corsX824.getFirst().contains(position)) {x = corsX824.getSecond();}
    	return x;
    }
    public static Integer getYMidgame(Integer position, Boolean isNotAlone) {
    	Integer y = null;
    	if(row1.getFirst().contains(position)) {y = row1.getSecond();}
    	if(row2.getFirst().contains(position)) {y = row2.getSecond();}
    	if(row3.getFirst().contains(position)) {y = row3.getSecond();}
    	if(row4.getFirst().contains(position)) {y = row4.getSecond();}
    	if(row5.getFirst().contains(position)) {y = row5.getSecond();}
    	if(row6.getFirst().contains(position)) {y = row6.getSecond();}
    	if(row7.getFirst().contains(position)) {y = row7.getSecond();}
    	if(row8.getFirst().contains(position)) {y = row8.getSecond();}
    	if(row9.getFirst().contains(position)) {y = row9.getSecond();}
    	if(row10.getFirst().contains(position)) {y = row10.getSecond();}
    	if(row11.getFirst().contains(position)) {y = row11.getSecond();}
    	if(row12.getFirst().contains(position)) {y = row12.getSecond();}
    	if(row13.getFirst().contains(position)) {y = row13.getSecond();}
    	if(row14.getFirst().contains(position)) {y = row14.getSecond();}
    	if(row15.getFirst().contains(position)) {y = row15.getSecond();}
    	if(row16.getFirst().contains(position)) {y = row16.getSecond();}
    	if(row17.getFirst().contains(position)) {y = row17.getSecond();}
    	if(corsY2442.getFirst().contains(position)) {y = corsY2442.getSecond();}
    	if(corsY597.getFirst().contains(position)) {y = corsY597.getSecond();}
    	return y;
    }
}
