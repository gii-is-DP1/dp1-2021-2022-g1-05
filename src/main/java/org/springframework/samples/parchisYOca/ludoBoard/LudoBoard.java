package org.springframework.samples.parchisYOca.ludoBoard;


import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Entity
public class LudoBoard extends BaseEntity {

    private String background;
    @Positive
    private Integer width;
    @Positive
    private Integer height;

    public LudoBoard(){
        this.background="../resources/static/resources/images/tablero parchís gaming set 4K punjabi no virus.png";
        this.width=1000;
        this.height=1000;
    }

    @OneToOne(mappedBy = "board")
    private LudoMatch match;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "board")
    @Size(max=16)
    private Set<LudoChip> chips;


}
