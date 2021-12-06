package org.springframework.samples.parchisYOca.gooseBoard;

import java.util.*;

import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
public class GooseBoard extends BaseEntity {

    private String background;
    @Positive
    private Integer width;
    @Positive
    private Integer height;

    @OneToOne(mappedBy = "board")
    private GooseMatch match;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board")
    @Size(max=4) //TODO Change before submitting
    private Set<GooseChip> chips;

    public Integer rollDice() {
        Integer rnd = new Random().ints(1,1,6).findFirst().getAsInt();
        return rnd;
    }


}

