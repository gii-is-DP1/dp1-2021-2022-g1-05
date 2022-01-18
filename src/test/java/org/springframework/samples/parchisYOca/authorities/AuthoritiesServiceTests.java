package org.springframework.samples.parchisYOca.authorities;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.user.Authorities;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class AuthoritiesServiceTests {

    private final String USERNAME = "pedro";
    private final String NOT_EXISTING_USERNAME = "ABC";
    private final String ROLE = "Admin";

    @Autowired
    protected AuthoritiesService authoritiesService;
    @Autowired
    protected UserService userService;

    @Test
    @Transactional
    public void testSaveAuthorityExistingUser() throws DataAccessException {
        Authorities authorities = authoritiesService.saveAuthorities(USERNAME, ROLE);
        assertThat(userService.findUserByUsername(USERNAME).isPresent());
        assertThat(userService.findUserByUsername(USERNAME).get().getAuthorities().size()).isEqualTo(2);
        assertThat(userService.findUserByUsername(USERNAME).get().getAuthorities().contains(authorities));
    }

    @Test
    @Transactional
    public void testSaveAuthorityNotExistingUser() throws DataAccessException {

        assertThrows(DataAccessException.class, () ->{
            Authorities authorities = authoritiesService.saveAuthorities(NOT_EXISTING_USERNAME, ROLE);
        });

    }

}
