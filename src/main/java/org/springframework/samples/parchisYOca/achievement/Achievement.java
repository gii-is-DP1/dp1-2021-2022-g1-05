package org.springframework.samples.parchisYOca.achievement;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.samples.parchisYOca.model.BaseEntity;
import org.springframework.samples.parchisYOca.model.NamedEntity;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "achievements")
public class Achievement extends BaseEntity {

    @Size(min = 3, max = 50)
    @Column(name = "name", unique=true)
    private String name;

    @NotEmpty
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Player> players;

}
