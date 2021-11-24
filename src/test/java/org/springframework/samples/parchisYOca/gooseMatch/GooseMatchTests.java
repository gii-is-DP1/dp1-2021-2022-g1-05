package org.springframework.samples.parchisYOca.gooseMatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
public class GooseMatchTests {
    @Autowired
    private GooseMatchService gooseMatchService;

    @Test
    public void testGooseMatchGetByIdP() {
        assertDoesNotThrow(()->gooseMatchService.findGooseMatchById(1));
    }
    @Test
    public  void testGooseMatchByIdN() {
        assertThrows(ConstraintViolationException.class, () -> {
            gooseMatchService.findGooseMatchById(-1);
        });
    }
    @Test
    public void testGooseMatchSaveNew() {
        GooseMatch match = new GooseMatch();
        assertDoesNotThrow(() -> gooseMatchService.save(match));
    }
    @Test
    public void testGooseMatchSaveWithCode() {
        String matchCode = RandomStringGenerator.getRandomString(6);
        GooseMatch match = new GooseMatch();
        match.setMatchCode(matchCode);
        assertDoesNotThrow(()->gooseMatchService.save(match));

    }
    @Test
    public void testGooseMatchByCodeP() {
        String matchCode = RandomStringGenerator.getRandomString(6);
        GooseMatch match = new GooseMatch();
        match.setMatchCode(matchCode);
        gooseMatchService.save(match);
        assertDoesNotThrow(()->gooseMatchService.findGooseMatchByMatchCode(matchCode));
    }
    @Test
    public void testGooseMatchByCodeN() {
        String matchCode = "o";
        assertThrows(EmptyResultDataAccessException.class, ()-> {
            gooseMatchService.findGooseMatchByMatchCode(matchCode);
        });
    }

}
