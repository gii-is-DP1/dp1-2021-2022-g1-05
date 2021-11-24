package org.springframework.samples.parchisYOca.web;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class WelcomeController {

    private final PlayerService playerService;

    @Autowired
    public WelcomeController(PlayerService playerService, UserService userService, AuthoritiesService authoritiesService) {
        this.playerService = playerService;
    }


	  @GetMapping({"/","/welcome"})
	  public String welcome(ModelMap model) {

          //Used to load the logged user to the model
          Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          if(authentication.getPrincipal().toString() != "anonymousUser"){
              if (authentication.isAuthenticated()){
                  User currentUser = (User) authentication.getPrincipal();
                  Player player = playerService.findPlayerByUsername(currentUser.getUsername());
                  model.put("playerId", player.getId());
              }

          }
          System.out.println(model.get("message"));
	    return "welcome";
	  }
}
