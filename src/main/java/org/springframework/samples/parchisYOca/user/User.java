package org.springframework.samples.parchisYOca.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.samples.parchisYOca.model.NamedEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
@Table(name="users")
public class User extends NamedEntity {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Length(min=1, max=12)
    private String username;

    @NotEmpty
    @Length(min=7)
    @Pattern(regexp = ".*[0-9].*")
    private String password;

    @URL
    private String avatarURL;

    boolean enabled;

}
