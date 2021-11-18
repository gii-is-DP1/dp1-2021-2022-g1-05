package org.springframework.samples.parchisYOca.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.Optional;

@Controller
@RequestMapping("/achievements")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping()
    public String listadoLogros(ModelMap modelMap){
        String vista = "achievements/listAchievements";
        Iterable<Achievement> achievements = achievementService.findAll();
        modelMap.addAttribute("achievements",achievements);
        return vista;
    }

    @GetMapping(path="/new")
    public String crearLogro(ModelMap modelMap){
        String view = "achievements/editAchievement";
        modelMap.addAttribute("achievement", new Achievement());
        return view;
    }

    @PostMapping(path="/save")
    public String salvarLogro(@Valid Achievement achievement, BindingResult result, ModelMap modelMap){
        String view="achievements/listAchievements";
        if(result.hasErrors()){
            modelMap.addAttribute("achievement",achievement);
            return "achievements/editAchievement";
        }else{
            achievementService.save(achievement);
            modelMap.addAttribute("message", "Achievement successfully saved!");
            view=listadoLogros(modelMap);
        }
        return view;
    }

    @GetMapping(path="/delete/{achievementId}")
    public String borrarLogro(@PathVariable("achievementId") int achievementId, ModelMap modelMap){
        String view = "achievements/listAchievements";
        Optional<Achievement> achievement = achievementService.findAchievementById(achievementId);
        if(achievement.isPresent()){
            achievementService.delete(achievement.get());
            modelMap.addAttribute("message", "Achievement successfully deleted!");
            view=listadoLogros(modelMap);

        }else{
            modelMap.addAttribute("message", "Achievement not found!");
            view=listadoLogros(modelMap);
        }
        return view;
    }
}
