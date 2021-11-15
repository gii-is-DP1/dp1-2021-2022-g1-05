package org.springframework.samples.parchisYOca.player;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.parchisYOca.model.Person;
import org.springframework.samples.parchisYOca.user.Authorities;
import org.springframework.samples.parchisYOca.user.User;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="players")
public class Player extends Person {

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "players")
    private Set<Achievement> achievements;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

}

