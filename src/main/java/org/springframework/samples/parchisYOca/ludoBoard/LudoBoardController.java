package org.springframework.samples.parchisYOca.ludoBoard;

import org.hibernate.envers.internal.tools.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class LudoBoardController {

    private final UserService userService;
    private final PlayerLudoStatsService playerLudoStatsService;
    private final LudoChipService ludoChipService;
    private final LudoMatchService ludoMatchService;

    public static final int INDICE_PRIMER_DADO = 0;
    public static final int INDICE_SEGUNDO_DADO = 1;
    public static final int INDICE_SUMA_DADOS = 2;

    private int[] dicesToCheck;

    @Autowired
    public LudoBoardController(UserService userService, PlayerLudoStatsService playerLudoStatsService, LudoChipService ludoChipService,
                                LudoMatchService ludoMatchService){
        this.userService=userService;
        this.playerLudoStatsService = playerLudoStatsService;
        this.ludoChipService = ludoChipService;
        this.ludoMatchService = ludoMatchService;
    }

    public void populateModel(Map<String, Object> model, Integer matchId, int[] dicesToShow) {
        LudoMatch match = ludoMatchService.findludoMatchById(matchId).get();
        model.put("stats", match.getStats());
        model.put("chips", ludoChipService.findChipsByMatchId(matchId));
        model.put("firstDice", dicesToShow[INDICE_PRIMER_DADO]);
        model.put("secondDice", dicesToShow[INDICE_SEGUNDO_DADO]);
        model.put("sumDice", dicesToShow[INDICE_SUMA_DADOS]);
        model.put("ludoBoard", ludoMatchService.findludoMatchById(match.getId()).get().getBoard());
    }

    @GetMapping(value="/ludoInGame/dicesRolled")
    public String ludoDicesRolled(HttpSession session, Map<String, Object> model){
        Boolean logged = userService.isAuthenticated();

        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            Integer matchId = (Integer) session.getAttribute("matchId");
            int[] dicesToShow = (int[])session.getAttribute("dices");

            PlayerLudoStats inGamePlayerStats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(
                authenticatedUser.getUsername(), matchId).get();
            Set<LudoChip> ludoChips = new HashSet<>(ludoChipService.findChipsByMatchId(matchId));
            Integer numberOfPlayers = ludoChips.size();

            //putting things in the model
            model.put("dicesRolled", 1);
            model.put("diceIndex", 0);
            populateModel(model, matchId, dicesToShow);

            dicesToCheck = dicesToShow;

            for(LudoChip lc : ludoChips){
                Integer inGameId = inGamePlayerStats.getInGameId();
                //To check that the chip belongs to the player
                if(lc.getInGamePlayerId() == inGameId){
                    LudoChip loggedPlayerChip = lc;


                    //Comprobar si ha sacado 5 y tiene fichas en base
                    if(dicesToCheck[INDICE_PRIMER_DADO] == 5 || dicesToCheck[INDICE_SEGUNDO_DADO] == 5 ||
                        dicesToCheck[INDICE_SUMA_DADOS] == 5) {
                        Integer diceCode = ludoChipService.manageFives(inGameId,matchId, dicesToCheck[INDICE_PRIMER_DADO], dicesToCheck[INDICE_SEGUNDO_DADO]);
                        model.put("diceCode", diceCode);
                        if(diceCode==0){
                            dicesToCheck[INDICE_PRIMER_DADO]=0;
                            dicesToCheck[INDICE_SUMA_DADOS]=dicesToCheck[INDICE_SUMA_DADOS]-dicesToCheck[INDICE_PRIMER_DADO];
                        }
                        else if(diceCode==1){
                            dicesToCheck[INDICE_SEGUNDO_DADO]=0;
                            dicesToCheck[INDICE_SUMA_DADOS]=dicesToCheck[INDICE_SUMA_DADOS]-dicesToCheck[INDICE_SEGUNDO_DADO];
                        }
                        else if(diceCode==2){
                            dicesToCheck[INDICE_PRIMER_DADO]=0;
                            dicesToCheck[INDICE_SEGUNDO_DADO]=0;
                            dicesToCheck[INDICE_SUMA_DADOS]=0;

                        } else if(diceCode == 3){
                            //TODO cuando saque dos cincos tiene que no dejarte tener el turno y tirar de nuevo los dados
                            dicesToCheck[INDICE_PRIMER_DADO]=0;
                            dicesToCheck[INDICE_SEGUNDO_DADO]=0;
                            dicesToCheck[INDICE_SUMA_DADOS]=0;
                            return "redirect:/ludoMatches/" + matchId;
                        }
                    }
                }
            }

            return "matches/ludoMatch";
        }else{
            return "redirect:/";
        }
    }

    @GetMapping(value = "/ludoInGame/chooseChip/{diceIndex}")
    public String ludoChooseChip(@PathVariable("diceIndex") Integer diceIndex, ModelMap model, HttpSession session) {
        Boolean logged = userService.isAuthenticated();

        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            Integer matchId = (Integer) session.getAttribute("matchId");
            int[] dicesToShow = (int[])session.getAttribute("dices");

            model.addAttribute("diceIndex", diceIndex);
            LudoMatch match = ludoMatchService.findludoMatchById(matchId).get();
            model.put("thisPlayerStats", playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(),matchId).get());

            populateModel(model, matchId, dicesToShow);
            return "matches/ludoMatch";
        } else {
            return "redirect:/";
        }

    }

    @GetMapping(value = "/ludoInGame/sumDice/{diceIndex}/{inGameChipId}")
    public String ludoSumDice(@PathVariable("diceIndex") Integer diceIndex, @PathVariable("inGameChipId") Integer inGameChipId,
                              ModelMap model) {
        Boolean logged = userService.isAuthenticated();

        if(logged==true) {
            boolean flagDobles = dicesToCheck[INDICE_PRIMER_DADO] == dicesToCheck[INDICE_SEGUNDO_DADO];
            return "matches/ludoMatch";
        } else {
            return "redirect:/";
        }


    }


}
