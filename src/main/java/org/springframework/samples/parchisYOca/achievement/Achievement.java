package org.springframework.samples.parchisYOca.achievement;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Audited
@Table(name = "achievements")
public class Achievement extends BaseEntity {

    @NotEmpty
    @Size(min = 3, max = 50)
    @Column(name = "name")
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    private String numberToBeat;

}
