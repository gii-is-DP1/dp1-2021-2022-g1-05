package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.samples.parchisYOca.user.User;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class GooseMatchController {

    private static final Integer MATCH_CODE_LENGTH = 5;

    private final GooseMatchService gooseMatchService;
    private final PlayerService playerService;

    @Autowired
    public GooseMatchController(GooseMatchService gooseMatchService, PlayerService playerService){
        this.gooseMatchService = gooseMatchService;
        this.playerService = playerService;
    }

    @GetMapping(value = "/gooseMatches/new")
    public String initCreationMatch(Map<String, Object> model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Player player = playerService.findPlayerByUsername(user.getUsername());

        GooseMatch gooseMatch = new GooseMatch();
        Date date = new Date();
        gooseMatch.setStartDate(date);
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        gooseMatch.setMatchCode(matchCode);
        gooseMatchService.saveGooseMatchWithPlayer(gooseMatch, player);
        model.put("match", gooseMatch);

        return "redirect:/";
    }
}
