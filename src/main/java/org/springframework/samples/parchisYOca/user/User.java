package org.springframework.samples.parchisYOca.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;
import org.springframework.samples.parchisYOca.model.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString
@Audited
@Table(name="users")
public class User implements Serializable { //Implementing serializable to fix an issue


    @Id
    @NotEmpty
    private String username;

    @NotEmpty
    @Length(min=7)
    @Pattern(regexp = ".*[0-9].*")
    private String password;

    boolean enabled;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Authorities> Authorities;


}
