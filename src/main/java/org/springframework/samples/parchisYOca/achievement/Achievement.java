package org.springframework.samples.parchisYOca.achievement;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.samples.parchisYOca.model.NamedEntity;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.user.User;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "achievements")
public class Achievement extends NamedEntity {

    @NotEmpty
    private String description;

    @NotEmpty
    @URL
    private String badgeURL;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Player> players;

}
