package org.springframework.samples.parchisYOca.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.samples.parchisYOca.achievement.exceptions.AchievementAlreadyExists;
import org.springframework.samples.parchisYOca.achievement.exceptions.NameAlreadyExists;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/achievements")
public class AchievementController {

    private final List<String> GOOSE_DESCRIPTIONS = List.of("Number of times landed on goose squares",
        "Number of times landed on dice squares", "Number of goose games won");
    private final List<String> LUDO_DESCRIPTIONS = List.of("Number of eaten tokens",
        "Number of walked squares", "Number of ludo games won");
    private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 5;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @GetMapping()
    public String listadoLogros(@RequestParam String page, ModelMap modelMap){
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.asc("name")));
        String vista = "achievements/listAchievements";
        Slice<Achievement> achievements = achievementService.findAllPaging(pageable);
        modelMap.addAttribute("achievements",achievements.getContent());
        modelMap.addAttribute("numberOfPages", Math.ceil(achievementService.findAll().size()/NUMBER_OF_ELEMENTS_PER_PAGE));
        Boolean logged = userService.isAuthenticated();

        if(logged==true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            List<GrantedAuthority> authorities = new ArrayList<>(authenticatedUser.getAuthorities());
            Boolean isAdmin = false;
            if(authorities.get(0).toString().equals("admin")){
                isAdmin = true;
            }
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            modelMap.addAttribute("unlockedAchievements", player.getAchievements());
            modelMap.addAttribute("isAdmin", isAdmin);
        }
        return vista;
    }

    @GetMapping(path="/newAchievement/{game}")
    public String crearLogro(@PathVariable("game") String game, ModelMap modelMap){
        String view = "achievements/createAchievement";
        if(game.equals("goose")){
            modelMap.addAttribute("descriptions", GOOSE_DESCRIPTIONS);
        }else {
            modelMap.addAttribute("descriptions", LUDO_DESCRIPTIONS);
        }
        modelMap.addAttribute("achievement", new Achievement());
        return view;
    }

    @PostMapping(path="/newAchievement/{game}")
    public String salvarLogro(@PathVariable("game") String game,
                              @Valid Achievement achievement, BindingResult result, ModelMap modelMap){
        modelMap.addAttribute("achievement",achievement);
        if(game.equals("goose")){
            modelMap.addAttribute("descriptions", GOOSE_DESCRIPTIONS);
        }else {
            modelMap.addAttribute("descriptions", LUDO_DESCRIPTIONS);
        }

        if(result.hasErrors()){
            return "achievements/createAchievement";
        }else{
            try{
                achievementService.save(achievement);
            }catch (NumberFormatException ex){
                modelMap.addAttribute("message", "The number to beat must be a number");
                return "achievements/createAchievement";
            }
            catch (AchievementAlreadyExists ex){
                modelMap.addAttribute("message", "The achievement has the same description and number as another");
                return "achievements/createAchievement";
            }
            catch (NameAlreadyExists ex){
                modelMap.addAttribute("message", "The name of the achievement already exists");
                return "achievements/createAchievement";
            }

        }
        return "redirect:/achievements?page=0";
    }

    @GetMapping(path="/delete/{achievementId}")
    public String borrarLogro(@PathVariable("achievementId") int achievementId){
        String view = "achievements/listAchievements";
        Optional<Achievement> achievement = achievementService.findAchievementById(achievementId);
        if(achievement.isPresent()){
            achievementService.delete(achievement.get());
        }
        return view;
    }
}
