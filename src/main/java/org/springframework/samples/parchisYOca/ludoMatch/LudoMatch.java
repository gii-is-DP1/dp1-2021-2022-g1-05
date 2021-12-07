package org.springframework.samples.parchisYOca.ludoMatch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Table(name="ludoMatches")
public class LudoMatch extends BaseEntity {

    @DateTimeFormat
    private Date startDate = null;

    @DateTimeFormat
    private Date endDate = null;

    @NotEmpty
    @Size(max=6)
    @Column(unique = true)
    private String matchCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ludoMatch")
    @Size(min=1, max=4)
    private Set<PlayerLudoStats> stats;

    @OneToOne
    private LudoBoard board;
}
