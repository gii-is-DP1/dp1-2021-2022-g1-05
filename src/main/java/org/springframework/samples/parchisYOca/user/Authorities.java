package org.springframework.samples.parchisYOca.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.samples.parchisYOca.model.BaseEntity;
import org.springframework.samples.parchisYOca.user.User;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "authorities")
public class Authorities extends BaseEntity{

	@ManyToOne
    @JoinColumn(name = "email")
    User user;

	@Size(min = 3, max = 50)
	String authority;


}
