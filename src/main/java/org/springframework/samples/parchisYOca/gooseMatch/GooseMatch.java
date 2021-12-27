package org.springframework.samples.parchisYOca.gooseMatch;

import lombok.Getter;
import lombok.Setter;

import lombok.ToString;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.petclinic.model.BaseEntity;

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
@Table(name="gooseMatches")
public class GooseMatch extends BaseEntity {

    @DateTimeFormat
    private Date startDate = null;

    @DateTimeFormat
    private Date endDate = null;

    private Integer closedLobby = 0;

    @NotEmpty
    @Size(max=6)
    @Column(unique = true)
    private String matchCode;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gooseMatch", orphanRemoval = true)
    @Size(max=4)
    private Set<PlayerGooseStats> stats;

    @NotAudited
    @OneToOne
    private GooseBoard board;
}
