package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class LudoMatchController {

    private final LudoMatchService ludoMatchService;
    private final PlayerService playerService;

    private static final Integer MATCH_CODE_LENGTH = 6;

    @Autowired
    public LudoMatchController(LudoMatchService ludoMatchService, PlayerService playerService, UserService userService, AuthoritiesService authoritiesService){
        this.ludoMatchService = ludoMatchService;
        this.playerService = playerService;
    }

    //TODO not showing you already have a match error
    @GetMapping("/ludoMatches/new")
    public String createMatch(ModelMap modelMap){
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        LudoMatch ludoMatch = new LudoMatch();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername());

        List<LudoMatch> playerInLudoMatches = new ArrayList<>(ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername()));
        if(playerInLudoMatches.size() != 0){
            LudoMatch playerInLudoMatch = playerInLudoMatches.get(0);
            modelMap.addAttribute("message", "You are already at a lobby: "+playerInLudoMatch.getMatchCode());
            return "redirect:/";
        }

        ludoMatch.setMatchCode(matchCode);
        ludoMatchService.saveludoMatchWithPlayer(ludoMatch, player);
        return "redirect:/ludoMatches/lobby/"+matchCode;
    }

    @GetMapping(value = "/ludoMatches/join")
    public String  joinLudoMatchForm(Map<String, Object> model){
        LudoMatch match = new LudoMatch();
        model.put("match", match);
        return "matches/joinMatchForm";
    }

    @PostMapping(value = "/ludoMatches/join")
    public String joinLudoMatch(@RequestParam String matchCode, ModelMap modelMap){
        Optional<LudoMatch> ludoMatch = ludoMatchService.findludoMatchByMatchCode(matchCode);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername());

        List<LudoMatch> playerInLudoMatches = new ArrayList<>(ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername()));


        if(playerInLudoMatches.size()!=0){ //If player is in a match
            if(matchCode.equals(playerInLudoMatches.get(0).getMatchCode())){   //If the player enters the lobby they are in
                return "redirect:/ludoMatches/lobby/" + matchCode;
            }else{
                modelMap.addAttribute("message", "You are already at a lobby: "+playerInLudoMatches.get(0).getMatchCode());
            }
        } else{
            if(ludoMatch.isPresent()) { //If the game exists
                if(!(ludoMatch.get().getStats().size()>=4)) { //If the game is not full
                    ludoMatchService.saveludoMatchWithPlayer(ludoMatch.get(),player);
                    return "redirect:/ludoMatches/lobby/"+matchCode;
                }else{
                    modelMap.addAttribute("message", "The lobby is full!");
                }
            }else{
                modelMap.addAttribute("message", "Lobby not found!");
            }
        }
        return "matches/joinMatchForm";
    }


    @GetMapping(value = "/ludoMatches/lobby/{matchCode}")
    public String initCreationLobby(@PathVariable("matchCode") String matchCode, ModelMap modelMap, HttpServletResponse response) {
        response.addHeader("Refresh", "5");

        LudoMatch ludoMatch = ludoMatchService.findludoMatchByMatchCode(matchCode).get();

        modelMap.addAttribute("stats", ludoMatch.getStats());
        modelMap.addAttribute("matchCode", matchCode);
        modelMap.addAttribute("match", ludoMatch);
        modelMap.addAttribute("matchId",ludoMatch.getId());

        return "matches/ludoMatchLobby";
    }

    @GetMapping(value = "/ludoMatches/{matchId}")
    public String showMatch(@PathVariable("matchId") Integer matchId){
        String view = "matches/ludoMatch";
        LudoMatch match = ludoMatchService.findludoMatchById(matchId);
        match.setStartDate(new Date());
        ludoMatchService.save(match);
        return view;
    }

    @GetMapping(value="/ludoMatches")
    public String listadoPartidas(ModelMap modelMap){
        String vista = "matches/listLudoMatches";
        Iterable<LudoMatch> ludoMatches = ludoMatchService.findAll();
        modelMap.addAttribute("ludoMatches",ludoMatches);
        return vista;
    }
}
