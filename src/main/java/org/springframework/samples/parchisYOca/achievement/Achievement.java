package org.springframework.samples.parchisYOca.achievement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.parchisYOca.model.BaseEntity;
import org.springframework.samples.parchisYOca.player.Player;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@Table(name = "achievements")
public class Achievement extends BaseEntity {

    @NotEmpty
    @Size(min = 3, max = 50)
    @Column(name = "name", unique=true)
    private String name;

    @NotEmpty
    private String description;

    @ManyToMany
    private Set<Player> players;

}
