package org.springframework.samples.parchisYOca.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.petclinic.pet.PetValidator;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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

    @InitBinder("user")
    public void initUserBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new UserValidator());
    }



    @GetMapping(value = "/users/new")
    public String initCreationForm(Map<String, Object> model) {
        Player player = new Player();
        model.put("player", player);
        return VIEWS_PLAYER_CREATE_FORM;
    }

    @PostMapping(value = "/users/new")
    public String processCreationForm(@Valid Player player, BindingResult result) {
        if (result.hasErrors()) {
            return VIEWS_PLAYER_CREATE_FORM;
        }
        else {
            //creating player, user, and authority
            this.playerService.savePlayer(player);
            return "redirect:/";
        }
    }


}
