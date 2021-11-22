package org.springframework.samples.parchisYOca.ludoMatch;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="LudoMatch")
public class LudoMatch extends BaseEntity {

    @NotEmpty
    @DateTimeFormat
    private Date StartDate;

    @NotEmpty
    @DateTimeFormat
    private Date EndDate;
}
