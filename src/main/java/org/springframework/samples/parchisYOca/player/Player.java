package org.springframework.samples.parchisYOca.player;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.parchisYOca.model.Person;
import org.springframework.samples.parchisYOca.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Audited
@Table(name="players")
public class Player extends Person implements Serializable { //Implementing serializable to fix an issue

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Achievement> achievements;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username", referencedColumnName = "username")
    private User user;

}

