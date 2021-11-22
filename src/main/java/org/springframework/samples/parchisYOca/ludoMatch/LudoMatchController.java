package org.springframework.samples.parchisYOca.ludoMatch;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.ModelMap;

import javax.validation.Valid;
import java.util.Optional;

@Controller
<<<<<<< HEAD
@RequestMapping ("/ludoMatch")
=======
@RequestMapping ("/LudoMatch")
>>>>>>> origin/javteroro

public class LudoMatchController {
    @Autowired
    private LudoMatchService ludoMatchService;

    @GetMapping()
    public String listadoPartidas(ModelMap modelMap){
<<<<<<< HEAD
        String vista="ludoMatch/matchList";
=======
        String vista="LudoMatch/matchList";
>>>>>>> origin/javteroro
        Iterable<LudoMatch> matchList = ludoMatchService.findAll();
        modelMap.addAttribute("matches",matchList);
        return vista;
    }

    @GetMapping(path="/new")
    public String crearPartida(ModelMap modelMap){
<<<<<<< HEAD
        String view = "ludoMatch/createLudoMatch";
=======
        String view = "LudoMatch/createLudoMatch";
>>>>>>> origin/javteroro
        modelMap.addAttribute("LudoMatch", new LudoMatch());
        return view;
    }
    @PostMapping(path="/save")
    public String ModificarPartida(@Valid LudoMatch ludoMatch, BindingResult result, ModelMap modelMap){
<<<<<<< HEAD
        String view="ludoMatch/matchList";
=======
        String view="LudoMatch/matchList";
>>>>>>> origin/javteroro
        if(result.hasErrors()){
            modelMap.addAttribute("ludoMatch",ludoMatch);
            return "ludoMatch/editLudoMatch";
        }else{
            ludoMatchService.save(ludoMatch);
            modelMap.addAttribute("message", "Ludo match successfully saved!");
            view=listadoPartidas(modelMap);
        }
        return view;
    }

    @GetMapping(path="/delete/{ludoMatchId}")
    public String borrarPartida(@PathVariable("ludoMatchId") int ludoMatchId, ModelMap modelMap){
<<<<<<< HEAD
        String view = "ludoMatch/matchList";
=======
        String view = "LudoMatch/matchList";
>>>>>>> origin/javteroro
        Optional<LudoMatch> ludoMatch = ludoMatchService.findLudoMatchById(ludoMatchId);
        if(ludoMatch.isPresent()){
            ludoMatchService.delete(ludoMatch.get());
            modelMap.addAttribute("message", "Ludo match successfully deleted!");
            view=listadoPartidas(modelMap);

        }else{
            modelMap.addAttribute("message", "Ludo match not found!");
            view=listadoPartidas(modelMap);
        }
        return view;
    }

}
