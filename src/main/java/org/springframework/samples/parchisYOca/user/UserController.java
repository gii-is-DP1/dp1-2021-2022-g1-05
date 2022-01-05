package org.springframework.samples.parchisYOca.user;

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
        Player player = new Player();
        model.put("player", player);
        return VIEWS_PLAYER_CREATE_FORM;
    }


    @PostMapping(value = "/users/new")
    public String processCreationForm(@RequestParam(name="user.password") String password,
                                      @RequestParam(name="user.username") String username,
                                      @RequestParam(name="email") String email,
                                      @Valid Player player, BindingResult result, Map<String, Object> model) {

        if(password.length() < 7 || !password.matches(".*[0-9].*")){
            model.put("message", "The password must be at least 7 characters long and contain a number");
            return VIEWS_PLAYER_CREATE_FORM;
        } else if(username.length() < 1){
            model.put("message", "The username can't be empty");
            return VIEWS_PLAYER_CREATE_FORM;
        }else if (playerService.findPlayerByUsername(username).isPresent()){
            model.put("message", "That username is already taken");
            return VIEWS_PLAYER_CREATE_FORM;
        }else if (playerService.findPlayerByEmail(email).isPresent()){
            model.put("message", "That email is already taken");
            return VIEWS_PLAYER_CREATE_FORM;
        } else if(result.hasErrors()) {
        	return VIEWS_PLAYER_CREATE_FORM;
        } else {
            //creating player, user, and authority
            this.playerService.savePlayer(player);
            return "redirect:/";
        }
        

    }

}
