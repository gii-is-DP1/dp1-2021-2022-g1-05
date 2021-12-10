package org.springframework.samples.parchisYOca.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.springframework.samples.parchisYOca.user.UserRevisionListener;

import lombok.Setter;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
@Setter
@Entity
@RevisionEntity(UserRevisionListener.class)
public class UserRevEntity extends DefaultRevisionEntity {
	@Column(name = "user")
	private String username;
}
