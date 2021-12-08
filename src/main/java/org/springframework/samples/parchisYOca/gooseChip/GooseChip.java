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


    @Range(min=0, max=63)
    private Integer position = 0;

    private Integer isInGoal = 0;

    private Integer inGameId;

    //TODO tenemos que poner playerId en ludo

    @ManyToOne
    GooseBoard board;
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
