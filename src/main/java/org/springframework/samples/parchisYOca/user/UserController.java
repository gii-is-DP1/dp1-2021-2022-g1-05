package org.springframework.samples.parchisYOca.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Map;

@Controller
@Slf4j
public class UserController {

    private static final String VIEWS_PLAYER_CREATE_FORM = "users/createPlayerForm";

    private final PlayerService playerService;

    @Autowired
    public UserController(PlayerService gameService) {
        this.playerService = gameService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }



    @GetMapping(value = "/users/new")
    public String initCreationForm(Map<String, Object> model) {
    	log.info("Creaing new player");
        Player player = new Player();
        model.put("player", player);
        return VIEWS_PLAYER_CREATE_FORM;
    }


    @PostMapping(value = "/users/new")
    public String processCreationForm(@RequestParam(name="user.password") String password,
                                      @RequestParam(name="user.username") String username,
                                      @RequestParam(name="email") String email,
                                      @Valid Player player, BindingResult result, Map<String, Object> model) {
    	log.info("Posting new user with username {} and emai {}", username, email);
        if(password.length() < 7 || !password.matches(".*[0-9].*")){
            model.put("message", "The password must be at least 7 characters long and contain a number");
            return VIEWS_PLAYER_CREATE_FORM;
        } else if(username.length() < 1){
            model.put("message", "The username can't be empty");
            log.debug("The username was empty");
            return VIEWS_PLAYER_CREATE_FORM;
        }else if (playerService.findPlayerByUsername(username).isPresent()){
        	log.debug("Player with username {} already exists", username);
            model.put("message", "That username is already taken");
            return VIEWS_PLAYER_CREATE_FORM;
        }else if (playerService.findPlayerByEmail(email).isPresent()){
        	log.debug("Player with email {} already exists", email);
            model.put("message", "That email is already taken");
            return VIEWS_PLAYER_CREATE_FORM;
        } else if(result.hasErrors()) {
        	log.debug("The result has errors");
        	return VIEWS_PLAYER_CREATE_FORM;
        } else {
        	log.debug("Player {} is correct so we are saving", username);
            //creating player, user, and authority
            this.playerService.savePlayer(player);
            return "redirect:/";
        }


    }

}
