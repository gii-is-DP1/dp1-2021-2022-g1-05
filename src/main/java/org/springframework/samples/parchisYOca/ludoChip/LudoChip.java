package org.springframework.samples.parchisYOca.ludoChip;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.model.BaseEntity;

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

}
