package org.springframework.samples.parchisYOca.playerLudoStats;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.samples.parchisYOca.model.BaseEntity;
import org.springframework.samples.parchisYOca.player.Player;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@ToString
@Table(name="playerLudoStats")
public class PlayerLudoStats extends BaseEntity {

    private Integer eatenTokens = 0;
    private Integer doubleRolls = 0;
    private Integer greedyRolls = 0;
    private Integer scoredTokens = 0;
    private Integer takeOuts = 0;
    private Integer walkedSquares = 0;
    private Integer isOwner = 0;
    private Integer inGameId = 0;

    //Flags de control
    private Integer hasWon = 0; //This stat can pass the values [0..1] if used in the stats views to count total wins from player
    private Integer hasTurn = 0;
    private Integer playerLeft = 0;
    private Integer turnDoubleRolls = 0;
    private Integer lastChipMovedId = 0;

    @ManyToOne
    Player player;
}
