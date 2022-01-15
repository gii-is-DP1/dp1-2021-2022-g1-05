package org.springframework.samples.parchisYOca.ludoChip;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.samples.parchisYOca.util.Color;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.model.BaseEntity;

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
	private static final Integer X_HOME_R = 53;
	private static final Integer X_HOME_L= 633;
	private static final Integer Y_HOME_T = 53;
	private static final Integer Y_HOME_B = 633;
	
	

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
    		this.position = null;
    	}

    }
    public Integer getX() {
    	Integer x = null;
    	switch(gameState) {
    	case earlyGame:
    		x = getXHome() + getXHomeDislpacement();
    		break;
    	case midGame:
    		break;
    	case endGame:
    		break;
    	}
    	return x;
    }
    public Integer getY() {
    	Integer y = null;
    	switch(gameState) {
    	case earlyGame:
    		y = getYHome() + getYHomeDislpacement();
    		break;
    	case midGame:
    		break;
    	case endGame:
    		break;
    	}
    	return y;
    }
    public Integer getXHome() {
    	switch(inGamePlayerId) {
		case 0:
			return X_HOME_R;
		case 1:
			return X_HOME_R;
		case 2:
			return X_HOME_L;
		case 3:
			return X_HOME_L;
		default:
			return null;
    	}
    	
    }
    public Integer getYHome() {
    	switch(inGamePlayerId) {
		case 0:
			return Y_HOME_T;
		case 1:
			return Y_HOME_B;
		case 2:
			return Y_HOME_T;
		case 3:
			return Y_HOME_B;
		default:
			return null;
    	}
    }
    public Integer getXHomeDislpacement() {
    	switch(inGameChipId) {
    	case 1:
    		return HOME_DISPLACEMENT;
    	case 3:
    		return HOME_DISPLACEMENT;
    	default:
    		return 0;
    	}
    }
    public Integer getYHomeDislpacement() {
    	switch(inGameChipId) {
    	case 2:
    		return HOME_DISPLACEMENT;
    	case 3:
    		return HOME_DISPLACEMENT;
    	default:
    		return 0;
    	}
    }
    
    
}
