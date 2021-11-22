package org.springframework.samples.parchisYOca.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

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


    @GetMapping("/players/{playerId}")
    public ModelAndView showPlayer(@PathVariable("playerId") int playerId) {
        ModelAndView mav = new ModelAndView("players/playerDetails");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().toString() != "anonymousUser"){
            if(authentication.isAuthenticated()){
                User authenticatedUser = (User) authentication.getPrincipal();
                mav.addObject("authenticatedPlayer",playerService.findPlayerByUsername(authenticatedUser.getUsername()));
            }
        }
        mav.addObject(this.playerService.findPlayerById(playerId));
        mav.addObject(this.playerService.findPlayerById(playerId).getUser());
        return mav;
    }

    @GetMapping(value = "/players")
    public String showAllPlayers(ModelMap modelMap) {
        String vista = "players/listPlayers";
        Iterable<Player> players = playerService.findAll();
        modelMap.addAttribute("players", players);
        return vista;
    }


    @GetMapping(value = "/players/{playerId}/edit")
    public String initUpdatePlayerForm(@PathVariable("playerId") int playerId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal().toString() != "anonymousUser"){
            if(authentication.isAuthenticated()){
                User authenticatedUser = (User) authentication.getPrincipal();
                Player player = this.playerService.findPlayerByUsername(authenticatedUser.getUsername());
                if (playerId == player.getId() || player.getUser().getAuthorities().contains("admin")){
                    model.addAttribute(player);
                    return VIEWS_PLAYER_CREATE_OR_UPDATE_FORM;
                } else {
                }
            }

        }
        return "redirect:";

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
