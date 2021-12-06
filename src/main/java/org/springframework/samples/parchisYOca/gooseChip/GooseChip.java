package org.springframework.samples.parchisYOca.gooseChip;

import org.hibernate.validator.constraints.Range;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class GooseChip extends BaseEntity {

    @Range(min=1, max=63)
	private Integer position;

    //TODO
    private Integer playerId;

    @ManyToOne
    GooseBoard board;





}
