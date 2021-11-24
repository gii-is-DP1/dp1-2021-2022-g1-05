package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.util.*;

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

    //TODO not showing you already have a match error
    @GetMapping("/gooseMatches/new")
    public String createGooseMatch(ModelMap modelMap){
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        GooseMatch gooseMatch = new GooseMatch();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername());

        List<GooseMatch> playerInGooseMatches = new ArrayList<>(gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()));
        if(playerInGooseMatches.size() != 0){
            GooseMatch playerInGooseMatch = playerInGooseMatches.get(0);
            modelMap.addAttribute("message", "You are already at a lobby: "+playerInGooseMatch.getMatchCode());
            return "redirect:/";
        }

        gooseMatch.setMatchCode(matchCode);
        gooseMatchService.saveGooseMatchWithPlayer(gooseMatch, player);
        return "redirect:/gooseMatches/lobby/"+matchCode;
    }

    @GetMapping(value = "/gooseMatches/join")
    public String  joinGooseMatchForm(Map<String, Object> model){
        GooseMatch match = new GooseMatch();
        model.put("match", match);
        return "gooseMatches/joinMatchForm";
    }

    @PostMapping(value = "/gooseMatches/join")
    public String joinGooseMatch(@RequestParam String matchCode, ModelMap modelMap){
        Optional<GooseMatch> gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername());

        List<GooseMatch> playerInGooseMatches = new ArrayList<>(gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()));


        if(playerInGooseMatches.size()!=0){ //If player is in a match
            if(matchCode.equals(playerInGooseMatches.get(0).getMatchCode())){   //If the player enters the lobby they are in
                return "redirect:/gooseMatches/lobby/" + matchCode;
            }else{
                modelMap.addAttribute("message", "You are already at a lobby: "+playerInGooseMatches.get(0).getMatchCode());
            }
        } else{
            if(gooseMatch.isPresent()) { //If the game exists
                if(!(gooseMatch.get().getStats().size()>=4)) { //If the game is not full
                    gooseMatchService.saveGooseMatchWithPlayer(gooseMatch.get(),player);
                    return "redirect:/gooseMatches/lobby/"+matchCode;
                }else{
                    modelMap.addAttribute("message", "The lobby is full!");
                }
            }else{
                modelMap.addAttribute("message", "Lobby not found!");
            }
        }
        return "gooseMatches/joinMatchForm";
    }


    @GetMapping(value = "/gooseMatches/lobby/{matchCode}")
    public String initCreationLobby(@PathVariable("matchCode") String matchCode, ModelMap modelMap, HttpServletResponse response) {
        response.addHeader("Refresh", "5");

        GooseMatch gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode).get();

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
