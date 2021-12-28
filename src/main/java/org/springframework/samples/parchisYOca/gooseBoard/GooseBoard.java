package org.springframework.samples.parchisYOca.gooseBoard;

import java.util.*;

import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;

import lombok.Getter;
import lombok.Setter;
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

    public GooseBoard(){
        this.background="../resources/images/GooseBoard.png";
        this.width=1000;
        this.height=1000;
    }

    @OneToOne(mappedBy = "board")
    private GooseMatch match;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board")
    @Size(max=4)
    private Set<GooseChip> chips;



}

