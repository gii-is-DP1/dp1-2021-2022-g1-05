package org.springframework.samples.parchisYOca.user;

import lombok.Getter;
import lombok.Setter;
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
@Table(name="users")
public class User extends BaseEntity implements Serializable { //Implementing serializable to fix an issue

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
