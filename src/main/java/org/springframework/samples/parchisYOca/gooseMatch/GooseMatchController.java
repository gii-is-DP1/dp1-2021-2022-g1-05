package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Controller
public class GooseMatchController {

    private final GooseMatchService gooseMatchService;
    private final PlayerService playerService;

    private static final Integer MATCH_CODE_LENGTH = 6;

    @Autowired
    public GooseMatchController(GooseMatchService gooseMatchService, PlayerService playerService, UserService userService, AuthoritiesService authoritiesService){
        this.gooseMatchService = gooseMatchService;
        this.playerService = playerService;
    }

    @GetMapping("gooseMatches/new")
    public String createGooseMatch(){
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        GooseMatch gooseMatch = new GooseMatch();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername());

        gooseMatch.setMatchCode(matchCode);
        gooseMatchService.saveGooseMatchWithPlayer(gooseMatch, player);
        return "redirect:/gooseMatches/lobby/"+matchCode;
    }

    @GetMapping(value = "/gooseMatches/lobby/{matchCode}")
    public String initCreationLobby(@PathVariable("matchCode") String matchCode, ModelMap modelMap, HttpServletResponse response) {
        response.addHeader("Refresh", "5");

        GooseMatch gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode);

        modelMap.addAttribute("stats", gooseMatch.getStats());
        modelMap.addAttribute("matchCode", matchCode);
        modelMap.addAttribute("match", gooseMatch);
        modelMap.addAttribute("matchId",gooseMatch.getId());

        return "gooseMatches/gooseMatchLobby";
    }

    @GetMapping(value = "/gooseMatches/{matchId}")
    public String showMatch(@PathVariable("matchId") Integer matchId){
        String view = "gooseMatches/gooseMatch";
        GooseMatch match = gooseMatchService.findGooseMatchById(matchId);
        match.setStartDate(new Date());
        gooseMatchService.save(match);
        return view;
    }

    /*@GetMapping(value = "/players/{playerId}/edit")
    public String initUpdatePlayerForm(@PathVariable("playerId") int playerId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.getPrincipal().toString() != "anonymousUser"){    //To check if the user is logged
            if(authentication.isAuthenticated()){

                User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
                Player authenticatedPlayer = this.playerService.findPlayerByUsername(authenticatedUser.getUsername());
                List<GrantedAuthority> authorities = new ArrayList<>(authenticatedUser.getAuthorities()); //Gets lists of authorities

                if (playerId == authenticatedPlayer.getId() || authorities.get(0).toString().equals("admin")){
                    model.addAttribute("hasPermission", "true");  //To check if user has permission to see data
                    Player player = playerService.findPlayerById(playerId);
                    model.addAttribute(player);
                    return VIEWS_PLAYER_UPDATE_FORM;
                }
            }


        }
        return "redirect:";

    }

    @PostMapping(value = "/players/{playerId}/edit")
        public String processUpdatePlayerForm(@Valid Player player, BindingResult result,
                                         @PathVariable("playerId") int playerId) {
        if (result.hasErrors()) {
            return VIEWS_PLAYER_UPDATE_FORM;
        }
        else {
            player.setId(playerId);
            this.playerService.savePlayer(player);
            return "redirect:/players/{playerId}";
        }
    }*/
}
