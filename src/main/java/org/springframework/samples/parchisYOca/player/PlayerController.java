package org.springframework.samples.parchisYOca.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

@Controller
public class PlayerController {

    private static final String VIEWS_PLAYER_UPDATE_FORM = "players/UpdatePlayerForm";


    private final PlayerService playerService;
    private final UserService userService;
    private final GooseMatchService gooseMatchService;
    private final LudoMatchService ludoMatchService;

    @Autowired
    public PlayerController(PlayerService playerService, UserService userService, GooseMatchService gooseMatchService, LudoMatchService ludoMatchService) {
        this.playerService = playerService;
        this.userService = userService;
        this.gooseMatchService = gooseMatchService;
        this.ludoMatchService = ludoMatchService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @GetMapping("/players/ownProfile")
    public String redirectToProfile(){
        Boolean logged = userService.isAuthenticated();
        if(logged==true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            Player player = playerService.findPlayerByUsername(currentUser.getUsername()).get();
            Integer playerId = player.getId();
            return "redirect:/players/"+playerId;
        }
        return "redirect:/";

    }


    @GetMapping("/players/{playerId}")
    public ModelAndView showPlayer(@PathVariable("playerId") int playerId) {
        ModelAndView mav = new ModelAndView("players/playerDetails");
        Boolean logged = userService.isAuthenticated();

        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            List<GrantedAuthority> authorities = new ArrayList<>(authenticatedUser.getAuthorities()); //Gets lists of authorities

            if (gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()).isPresent() ||
                ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername()).isPresent()){
                mav.addObject("inGame","true"); //Not delete account when in game
            }
            if (playerId == player.getId() || authorities.get(0).toString().equals("admin")){
                mav.addObject("hasPermission", "true"); //To check if user has permission to see data
            }

        }
        mav.addObject(this.playerService.findPlayerById(playerId).get());
        mav.addObject(this.playerService.findPlayerById(playerId).get().getUser());
        return mav;
    }

    @GetMapping(value = "/players")
    public String showAllPlayers(ModelMap modelMap) {
        Iterable<Player> players = playerService.findAll();
        String playersInGame = "";
        for(Player p : players){
            if (gooseMatchService.findLobbyByUsername(p.getUser().getUsername()).isPresent() ||
            ludoMatchService.findLobbyByUsername(p.getUser().getUsername()).isPresent()){
                playersInGame = playersInGame + p.getUser().getUsername() + " ";
            }
        }
        modelMap.addAttribute("players", players);
        modelMap.addAttribute("playersInGame", playersInGame);
        return "players/listPlayers";
    }

    @PostMapping(value = "/players")
    public String filterPlayers(ModelMap modelMap, @RequestParam String Username) {
        String vista = "players/listPlayers";
        Iterable<Player> players = playerService.findAllFilteringByUsername(Username);
        modelMap.addAttribute("players", players);
        return vista;
    }


    @GetMapping(value = "/players/{playerId}/edit")
    public String initUpdatePlayerForm(@PathVariable("playerId") int playerId, Model model) {

        Boolean logged = userService.isAuthenticated();

        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player authenticatedPlayer = this.playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            List<GrantedAuthority> authorities = new ArrayList<>(authenticatedUser.getAuthorities()); //Gets lists of authorities

            if (playerId == authenticatedPlayer.getId() || authorities.get(0).toString().equals("admin")){
                model.addAttribute("hasPermission", "true");  //To check if user has permission to see data
                Player player = playerService.findPlayerById(playerId).get();
                model.addAttribute(player);
                return VIEWS_PLAYER_UPDATE_FORM;
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
}

    @GetMapping(path="/players/disable/{playerId}")
    public String disablePlayer(@PathVariable("playerId") int playerId, ModelMap modelMap){
        String view = "players/listPlayers";
        Player player = playerService.findPlayerById(playerId).get();
        if(!player.equals(null)){
            playerService.disable(player);
            modelMap.addAttribute("message", "Player successfully disabled!");
            view=showAllPlayers(modelMap);

        }else{
            modelMap.addAttribute("message", "Player not found!");
            view=showAllPlayers(modelMap);
        }
        return view;
    }

    @GetMapping(path="/players/enable/{playerId}")
    public String enablePlayer(@PathVariable("playerId") int playerId, ModelMap modelMap){
        String view = "players/listPlayers";
        Player player = playerService.findPlayerById(playerId).get();
        if(!player.equals(null)){
            playerService.enable(player);
            modelMap.addAttribute("message", "Player successfully enabled!");
            view=showAllPlayers(modelMap);

        }else{
            modelMap.addAttribute("message", "Player not found!");
            view=showAllPlayers(modelMap);
        }
        return view;
    }

    @GetMapping(path="/players/{playerId}/delete")
    public String deletePlayer(@PathVariable("playerId") int playerId, ModelMap modelMap){
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        Boolean logged = userService.isAuthenticated();

        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player authenticatedPlayer = this.playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();

            if(playerId == authenticatedPlayer.getId()){ //If auto-deletes, logs off
                authentication.setAuthenticated(false);
            }

            if(playerOptional.isPresent()){
                Player player = playerOptional.get();
                playerService.delete(player);
                modelMap.addAttribute("message", "Player successfully deleted!");

            }else{
                modelMap.addAttribute("message", "Player not found!");
            }
        }

        return "welcome";
    }

    @GetMapping(path="/players/{playerId}/ludoMatchesPlayed")
    public ModelAndView ludoMatchesOfPlayer(@PathVariable("playerId") int playerId){
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        ModelAndView mav = new ModelAndView("matches/listMatchesInProfile");
        Collection<LudoMatch> matches = ludoMatchService.findMatchesByUsername(playerOptional.get().getUser().getUsername());
        mav.addObject("matches", matches);
        return mav;
    }

    @GetMapping(path="/players/{playerId}/gooseMatchesPlayed")
    public ModelAndView gooseMatchesOfPlayer(@PathVariable("playerId") int playerId){
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        ModelAndView mav = new ModelAndView("matches/listMatchesInProfile");
        Collection<GooseMatch> matches = gooseMatchService.findMatchesByUsername(playerOptional.get().getUser().getUsername());
        mav.addObject("matches", matches);
        return mav;
    }

}
