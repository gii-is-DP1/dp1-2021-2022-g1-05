package org.springframework.samples.parchisYOca.ludoChip;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.util.Pair;
import org.springframework.samples.parchisYOca.gooseChip.Color;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class LudoChip  extends BaseEntity {

    private Integer position;

    @Range(min=0, max=3)
    private Integer playerId;
    //Para diferenciar las distintas fichas del mismo color
    private Integer inGameId;
    private String color; 
    //El juego depende mucho de en que parte del tablero se encuentra la ficha
    //este atributo simplifcara la logica mas tarde
    //earlyGame -> casilla de inicio -> sacar 5 para pasar al sguiente estado
    //midGame -> juego normal del parchis
    //endGame -> las casillas antes de llegar a la meta
    private GameState gameState = GameState.earlyGame;

    @ManyToOne
    LudoBoard board;
    
    public String getColor() { 
    	//El set no importa mucho porque el color siempre dependera del Id del jugador
    	Color[] colores = Color.values();
    	String color = colores[playerId].toString();
    	return color;
    }
    public void setPosition(Integer position) {
    	
    	switch(gameState) {
    	case earlyGame:
    		this.position = null; //esta en la casa
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
}
