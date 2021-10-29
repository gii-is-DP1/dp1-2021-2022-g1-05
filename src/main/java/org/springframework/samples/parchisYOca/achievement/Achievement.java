package org.springframework.samples.parchisYOca.achievement;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.parchisYOca.model.NamedEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "achievements")
public class Achievement extends NamedEntity {

    private String description;
    private String badgeURL;
}
