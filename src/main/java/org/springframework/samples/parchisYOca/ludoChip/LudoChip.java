package org.springframework.samples.parchisYOca.ludoChip;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.util.Pair;
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

    @Range(min=1, max=4)
    private Integer playerId;
    //Esto por si queremos guardar el color como tal o se podría asginar en el controlador sin almacenarlo
    //private String Color;

    @ManyToOne
    LudoBoard board;
    
    public Map<Integer, Pair<Integer, Integer>> posToPixelMap() {
    	Map<Integer, Pair<Integer, Integer>> pixelBoard = 
    			new HashMap<Integer, Pair<Integer, Integer>>();
    	/*aqui se construiría el mapa en base a la imagen,
    	 pero como no hay imagen definitiva no lo voy a hacer ahora*/
    	return pixelBoard;
    }
    public Pair<Integer, Integer> getBoardPosition(Integer position) { 
    	Map<Integer, Pair<Integer, Integer>> pixelBoard = posToPixelMap();
    	return pixelBoard.get(position);
    }
}
