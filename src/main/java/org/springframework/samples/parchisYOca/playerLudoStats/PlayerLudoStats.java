package org.springframework.samples.parchisYOca.playerLudoStats;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Audited
@Table(name="playerLudoStats")
public class PlayerLudoStats extends BaseEntity {

    private Integer eatenTokens = 0;
    private Integer doubleRolls = 0;
    private Integer greedyRolls = 0;
    private Integer createdBlocks = 0;
    private Integer scoredTokens = 0;
    private Integer takeOuts = 0;
    private Integer walkedSquares = 0;
    private Integer isOwner = 0;
    private Integer inGameId = 0;
    private Integer hasTurn = 0;

    @ManyToOne
    Player player;
    @ManyToOne
    LudoMatch ludoMatch;
}
