package org.springframework.samples.parchisYOca.ludoMatch;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Audited
@Table(name="ludoMatches")
public class LudoMatch extends BaseEntity {



    @DateTimeFormat
    private Date startDate = null;

    @DateTimeFormat
    private Date endDate = null;

    private Integer closedLobby = 0;

    @NotEmpty
    @Size(max=6)
    @Column(unique = true)
    private String matchCode;

    @OneToMany(mappedBy = "ludoMatch")
    @Size(min=1, max=4)
    private Set<PlayerLudoStats> stats;

    @NotAudited
    @OneToOne
    private LudoBoard board;



}
