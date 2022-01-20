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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
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

    @GetMapping("/achievements")
    public String listAchievements(@RequestParam String page, ModelMap modelMap){
        String vista = "welcome";
        Boolean logged = userService.isAuthenticated();

        if(logged==true) {
            Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.asc("name")));
            vista = "achievements/listAchievements";
            Slice<Achievement> achievements = achievementService.findAllPaging(pageable);
            modelMap.addAttribute("achievements",achievements.getContent());
            modelMap.addAttribute("numberOfPages", Math.ceil(achievementService.findAll().size()/NUMBER_OF_ELEMENTS_PER_PAGE));
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

    @GetMapping(path="/achievements/newAchievement/{game}")
    public String createAchivement(@PathVariable("game") String game, ModelMap modelMap){
        String view = "achievements/createAchievement";
        if(game.equals("goose")){
            modelMap.addAttribute("descriptions", GOOSE_DESCRIPTIONS);
        }else {
            modelMap.addAttribute("descriptions", LUDO_DESCRIPTIONS);
        }
        modelMap.addAttribute("achievement", new Achievement());
        return view;
    }

    @PostMapping(path="/achievements/newAchievement/{game}")
    public String saveAchievement(@PathVariable("game") String game,
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

    @GetMapping(path="/achievements/delete/{achievementId}")
    public String deleteAchievement(@PathVariable("achievementId") int achievementId){
        Optional<Achievement> achievement = achievementService.findAchievementById(achievementId);
        if(achievement.isPresent()){
            achievementService.delete(achievement.get());
        }
        return "redirect:/achievements?page=0";
    }
}
