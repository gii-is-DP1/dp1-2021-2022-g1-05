package org.springframework.samples.parchisYOca.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class WelcomeController {

    private final PlayerService playerService;
    private final GooseMatchService gooseMatchService;
    private final LudoMatchService ludoMatchService;

    private final Integer EMPTY = 0;

    @Autowired
    public WelcomeController(PlayerService playerService, GooseMatchService gooseMatchService, LudoMatchService ludoMatchService) {
        this.playerService = playerService;
        this.gooseMatchService = gooseMatchService;
        this.ludoMatchService = ludoMatchService;
    }


	  @GetMapping({"/","/welcome"})
	  public String welcome(ModelMap model, HttpSession session) {
          //Used to load the logged user to the model
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          if(authentication.getPrincipal().toString() != "anonymousUser"){
              if (authentication.isAuthenticated()){
                  User currentUser = (User) authentication.getPrincipal();
                  Player player = playerService.findPlayerByUsername(currentUser.getUsername()).get();
                  model.put("playerId", player.getId());

                  List<GooseMatch> playerInGooseMatches = new ArrayList<>(gooseMatchService.findLobbyByUsername(currentUser.getUsername()));
                  if(playerInGooseMatches.size() != EMPTY){
                      model.addAttribute("inGooseMatch", 1);
                  }

                  List<LudoMatch> playerInLudoMatches = new ArrayList<>(ludoMatchService.findLobbyByUsername(currentUser.getUsername()));
                  if(playerInLudoMatches.size() != EMPTY){
                      model.addAttribute("inLudoMatch", 1);
                  }

                  if(session.getAttribute("matchClosed") != null){
                      model.addAttribute("message", "Your game has been closed by an admin");
                  }

              }

          }
	    return "welcome";
	  }
}
