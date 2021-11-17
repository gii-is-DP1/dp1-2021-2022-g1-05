package org.springframework.samples.parchisYOca.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.samples.parchisYOca.user.UserValidator;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class PlayerController {

    private static final String VIEWS_PLAYER_CREATE_OR_UPDATE_FORM = "players/createOrUpdatePlayerForm";


    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService, UserService userService, AuthoritiesService authoritiesService) {
        this.playerService = playerService;
    }



    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }


    @GetMapping(value = "/players/new")
    public String initCreationForm(Map<String, Object> model) {
        Player player = new Player();
        model.put("player", player);
        return VIEWS_PLAYER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping(value = "/players/new")
    public String processCreationForm(@Valid Player player, BindingResult result) {
        if (result.hasErrors()) {
            return VIEWS_PLAYER_CREATE_OR_UPDATE_FORM;
        }
        else {
            //creating owner, user and authorities
            this.playerService.savePlayer(player);

            return "redirect:/players/" + player.getId();
        }
    }

    @GetMapping("/players/{playerId}")
    public ModelAndView showPlayer(@PathVariable("playerId") int playerId) {
        ModelAndView mav = new ModelAndView("players/playerDetails");
        mav.addObject(this.playerService.findPlayerById(playerId));
        mav.addObject(this.playerService.findPlayerById(playerId).getUser());
        return mav;
    }

    @GetMapping(value = "/players/{playerId}/edit")
    public String initUpdatePlayerForm(@PathVariable("playerId") int playerId, Model model) {
        Player player = this.playerService.findPlayerById(playerId);
        model.addAttribute(player);
        return VIEWS_PLAYER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping(value = "/players/{playerId}/edit")
    public String processUpdatePlayerForm(@Valid Player player, BindingResult result,
                                         @PathVariable("playerId") int playerId) {
        if (result.hasErrors()) {
            return VIEWS_PLAYER_CREATE_OR_UPDATE_FORM;
        }
        else {
            player.setId(playerId);
            this.playerService.savePlayer(player);
            return "redirect:/players/{playerId}";
        }
    }

}
