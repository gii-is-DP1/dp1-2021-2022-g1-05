package org.springframework.samples.parchisYOca.gooseGame;

import org.springframework.samples.parchisYOca.player.Player;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class GooseChip {
	private Integer position;
	private Player owner;
	
	
	public void setPosition(Integer position) {
		if(1<=position&&position<=63) {
			this.position = position;
		}
	}

}
