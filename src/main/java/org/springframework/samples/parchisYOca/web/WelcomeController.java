package org.springframework.samples.parchisYOca.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class WelcomeController {

    private final PlayerService playerService;
    private final GooseMatchService gooseMatchService;
    private final LudoMatchService ludoMatchService;
    private final UserService userService;


    @Autowired
    public WelcomeController(PlayerService playerService, GooseMatchService gooseMatchService,
                             LudoMatchService ludoMatchService, UserService userService) {
        this.playerService = playerService;
        this.gooseMatchService = gooseMatchService;
        this.ludoMatchService = ludoMatchService;
        this.userService = userService;
    }


	  @GetMapping({"/","/welcome"})
	  public String welcome(ModelMap model, HttpSession session) {
          //Used to load the logged user to the model
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          Boolean logged = userService.isAuthenticated();

          if(logged == true){
              User currentUser = (User) authentication.getPrincipal();
              Player player = playerService.findPlayerByUsername(currentUser.getUsername()).get();
              model.put("playerId", player.getId());

              if(session.getAttribute("ownerLeft") != null){
                  model.addAttribute("message", session.getAttribute("ownerLeft"));
                  session.removeAttribute("ownerLeft");
              }

              Optional<GooseMatch> playerInGooseMatches = gooseMatchService.findLobbyByUsername(currentUser.getUsername());
              if(playerInGooseMatches.isPresent()){
                  model.addAttribute("inGooseMatch", 1);
              }

              Optional<LudoMatch> playerInLudoMatches = ludoMatchService.findLobbyByUsername(currentUser.getUsername());
              if(playerInLudoMatches.isPresent()){
                  model.addAttribute("inLudoMatch", 1);
              }

              session.setAttribute("fromLudo", null);
              session.setAttribute("fromGoose", null);
          }
	    return "welcome";
	  }
}
