package org.springframework.samples.parchisYOca.playerGooseStats;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Audited
@Table(name="playerGooseStats")
public class PlayerGooseStats extends BaseEntity {

    private Integer landedGeese = 0;
    private Integer doubleRolls = 0;
    private Integer landedDice = 0;
    private Integer landedBridges = 0;
    private Integer landedJails = 0;
    private Integer landedInn = 0;
    private Integer landedMaze = 0;
    private Integer landedDeath = 0;
    private Integer isOwner = 0;
    private Integer hasWon = 0;
    private Integer inGameId = 0;
    private Integer hasTurn = 0;
    private Integer playerLeft = 0;

    @ManyToOne
    Player player;
    @ManyToOne
    GooseMatch gooseMatch;
}
