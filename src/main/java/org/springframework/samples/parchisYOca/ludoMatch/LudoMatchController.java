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
@RequestMapping ("/LudoMatch")

public class LudoMatchController {
    @Autowired
    private LudoMatchService ludoMatchService;

    @GetMapping()
    public String listadoPartidas(ModelMap modelMap){
        String vista="LudoMatch/matchList";
        Iterable<LudoMatch> matchList = ludoMatchService.findAll();
        modelMap.addAttribute("matches",matchList);
        return vista;
    }

    @GetMapping(path="/new")
    public String crearPartida(ModelMap modelMap){
        String view = "LudoMatch/createLudoMatch";
        modelMap.addAttribute("LudoMatch", new LudoMatch());
        return view;
    }
    @PostMapping(path="/save")
    public String ModificarPartida(@Valid LudoMatch ludoMatch, BindingResult result, ModelMap modelMap){
        String view="LudoMatch/matchList";
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
        String view = "LudoMatch/matchList";
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
