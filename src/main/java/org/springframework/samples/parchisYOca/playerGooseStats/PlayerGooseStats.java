package org.springframework.samples.parchisYOca.playerGooseStats;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.petclinic.model.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Entity
@ToString
@Table(name="playerGooseStats")
public class PlayerGooseStats extends BaseEntity {

    private Integer landedGeese = 0;
    private Integer doubleRolls = 0;
    private Integer landedDice = 0;
    private Integer landedBridges = 0;
    private Integer landedJails = 0;
    private Integer landedSpecials = 0;
    private Integer isOwner = 0;
    private Integer inGameId = 0;
    private Integer hasTurn = 0;

    @ManyToOne
    Player player;

    @ManyToOne
    GooseMatch gooseMatch;
}
