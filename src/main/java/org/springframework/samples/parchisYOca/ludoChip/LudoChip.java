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

    @Range(min=1, max=105)
    private Integer position;

    @Range(min=0, max=3)
    private Integer playerId;
    //Para diferenciar las distintas fichas del mismo color
    private Integer chipId;
    private String color; 
    //El juego depende mucho de en que parte del tablero se encuentra la ficha
    //este atributo simplifcara la logica mas tarde
    //earlyGame -> casilla de inicio -> sacar 5 para pasar al sguiente estado
    //midGame -> juego normal del parchis
    //endGame -> las casillas antes de llegar a la meta
    private GameState gameState;

    @ManyToOne
    LudoBoard board;
    
    public String getColor() { 
    	//El set no importa mucho porque el color siempre dependera del Id del jugador
    	Color[] colores = Color.values();
    	String color = colores[playerId].toString();
    	return color;
    }
    public void setPosition(Integer position) {
    	/*De esta forma podemos pasarle al seter una posicion+cantidad sin necesidad
    	 de hacer una logica extra en el controlador ya que el tablero es circular*/
    	switch(playerId) {
    	//El 1 es un placeholder hasta saber que tablero usamos
    	case 0:
    		position = position%105+1;
    		break;
    	case 1:
    		position = position%105+1;
    		break;
    	case 2:
    		position = position%105+1;
    		break;
    	case 3:
    		position = position%105+1;
    		break;
    	}
    	
    }
}
