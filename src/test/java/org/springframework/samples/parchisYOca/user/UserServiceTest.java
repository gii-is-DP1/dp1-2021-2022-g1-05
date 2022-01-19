package org.springframework.samples.parchisYOca.user;

import org.hibernate.dialect.function.CastFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.samples.parchisYOca.user.Authorities;
import testDataGenerator.TestDataGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class UserServiceTest {
	private static final Long USERS_IN_DB =7l;
	private static final String GOOD_UNAME = "Benjamin";
	private static final String BAD_UNAME = "";
	private static final String GOOD_PSSWRD = "QII2222N";
	private static final String NO_NUMBER_PSSWRD = "AAAAAAAAAA";
	private static final String BAD_LENGHT_PSSWRD ="O";
	private static final User RAN_USER = TestDataGenerator.generateUser(GOOD_UNAME, GOOD_PSSWRD);


	@Autowired
	private UserService uService;

	@Test
	@Transactional
	public void testFindAll() {
		Long count = uService.findAll().spliterator().estimateSize();
		assertThat(count).isEqualTo(USERS_IN_DB);
	}
	@Test
	@Transactional
	public void testSaveFindAndDeleteUser() {
		User u = new User();
		u.setUsername(GOOD_UNAME);
		u.setPassword(GOOD_PSSWRD);
		uService.saveUser(u);
		User u2 = uService.findUserByUsername(GOOD_UNAME).get();
		assertThat(u.getUsername()).isEqualTo(u2.getUsername());
		assertThat(u.getPassword()).isEqualTo(u2.getPassword());
		uService.deleteUser(u);
		Optional<User> uO = uService.findUserByUsername(GOOD_UNAME);
		assertThat(uO.isEmpty()).isEqualTo(true);
	}
	@Test
	@Transactional
	public void testIsAuthenticated() {
		uService.saveUser(RAN_USER);
		SecurityContextHolder contextHolder = new SecurityContextHolder();
		Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("admin");
		auths.add(authority);
		org.springframework.security.core.userdetails.User newUser = new org.springframework.security.core.userdetails.User(
				RAN_USER.getUsername(), RAN_USER.getPassword(), true, true, true, true, auths);

		Authentication principal = new UsernamePasswordAuthenticationToken(newUser,RAN_USER.getPassword(),auths);
		SecurityContext context = contextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		context.setAuthentication(principal);
		contextHolder.setContext(context);
		assertThat(uService.isAuthenticated()).isEqualTo(true);
	}
	@Test
	@Transactional
	public void testIsAuthenticatedNegativeUserDoesntExist() {
		SecurityContextHolder contextHolder = new SecurityContextHolder();
		Collection<GrantedAuthority> auths = new HashSet<GrantedAuthority>();
		GrantedAuthority authority = new SimpleGrantedAuthority("admin");
		auths.add(authority);
		org.springframework.security.core.userdetails.User newUser = new org.springframework.security.core.userdetails.User(
				RAN_USER.getUsername(), RAN_USER.getPassword(), true, true, true, true, auths);

		Authentication principal = new UsernamePasswordAuthenticationToken(newUser,RAN_USER.getPassword(),auths);
		SecurityContext context = contextHolder.getContext();
		context.setAuthentication(principal);
		contextHolder.setContext(context);
		assertThat(uService.isAuthenticated()).isEqualTo(false);
	}
	@Test
	@Transactional
	@WithMockUser(value="anonymousUser")
	public void testIsAuthenticatedNegativeUserWasNotLoggedIn() {
		uService.saveUser(RAN_USER);
		assertThat(uService.isAuthenticated()).isEqualTo(false);
	}

	//Negative
	@Test
	@Transactional
	public void testSaveUserNegative() {
		User u = new User();
		u.setUsername(BAD_UNAME);
		assertThrows(ConstraintViolationException.class,
				()->{uService.saveUser(u);});
		u.setUsername(GOOD_UNAME);
		u.setPassword(BAD_LENGHT_PSSWRD);
		assertThrows(ConstraintViolationException.class,
				()->{uService.saveUser(u);});
		u.setPassword(NO_NUMBER_PSSWRD);
		assertThrows(ConstraintViolationException.class,
				()->{uService.saveUser(u);});
	}
	@Test
	@Transactional
	public void testDeleteUserNegative() {
		User u = new User();
		assertThrows(DataAccessException.class,
				()->{uService.deleteUser(u);});
	}
}
