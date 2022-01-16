package org.springframework.samples.parchisYOca.ludoBoard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class LudoBoardController {

    private final UserService userService;
    private final PlayerLudoStatsService playerLudoStatsService;
    private final LudoChipService ludoChipService;
    private final LudoMatchService ludoMatchService;

    public static final Integer INDICE_PRIMER_DADO = 0;
    public static final Integer INDICE_SEGUNDO_DADO = 1;
    public static final Integer INDICE_SUMA_DADOS = 2;
    public static final Integer PRIMER_DADO_5=0;
    public static final Integer SEGUNDO_DADO_5=1;
    public static final Integer SUMA_DADOS_5=2;
    public static final Integer DOS_DADOS_5=3;

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
            Set<LudoChip> ludoChips = new HashSet<>(ludoChipService.findChipsByMatchId(matchId));

            PlayerLudoStats inGamePlayerStats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(
                authenticatedUser.getUsername(), matchId).get();
            Integer nextInGameId = (inGamePlayerStats.getInGameId()+1)%ludoChips.size()/4;
            PlayerLudoStats nextInGameStats = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(nextInGameId, matchId).get();

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
                        if(diceCode==PRIMER_DADO_5){
                            dicesToCheck[INDICE_PRIMER_DADO]=0;

                        }
                        else if(diceCode==SEGUNDO_DADO_5){
                            dicesToCheck[INDICE_SEGUNDO_DADO]=0;

                        }
                        else if(diceCode==SUMA_DADOS_5){
                            dicesToCheck[INDICE_PRIMER_DADO]=0;
                            dicesToCheck[INDICE_SEGUNDO_DADO]=0;
                            nextInGameStats.setHasTurn(1);
                            inGamePlayerStats.setHasTurn(0);
                            return "redirect:/ludoMatches/" + matchId;
                        } else if(diceCode == DOS_DADOS_5){
                            dicesToCheck[INDICE_PRIMER_DADO]=0;
                            dicesToCheck[INDICE_SEGUNDO_DADO]=0;
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
            if(dicesToCheck[diceIndex]==20){
                model.addAttribute("message","You ate a chip so you get another free 20 movements on the house");
            }
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
                              ModelMap model,HttpSession session) {
        Boolean logged = userService.isAuthenticated();

        if(logged==true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer matchId = (Integer) session.getAttribute("matchId");
            User authenticatedUser = (User) authentication.getPrincipal();

            PlayerLudoStats pls=playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(),matchId).get();
            Integer inGamePlayerId=pls.getInGameId();

            boolean flagDobles = dicesToCheck[INDICE_PRIMER_DADO] == dicesToCheck[INDICE_SEGUNDO_DADO];

            List<LudoChip> chips =new ArrayList<>(ludoChipService.findChipsByMatchId(matchId));

            LudoChip chip=ludoChipService.findConcreteChip(matchId,inGameChipId,inGamePlayerId).get();
            boolean hasEaten=ludoChipService.move(chip,dicesToCheck[diceIndex],chips,inGamePlayerId);


            Integer nextInGameId = (inGamePlayerId+1)%chips.size()/4;
            PlayerLudoStats nextInGameStats = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(nextInGameId, matchId).get();

            if(hasEaten){
                dicesToCheck[diceIndex]=20;
                return "redirect:/ludoInGame/chooseChip/"+diceIndex;
            }//TODO si ha llegado al final
            else if(checkDicesLeft()){
                dicesToCheck[diceIndex]=0;
                return "redirect:/ludoInGame/chooseChip/"+Integer.toString((diceIndex+1)%2);
            }
            else if(flagDobles){
                dicesToCheck[diceIndex]=0;
                return "redirect:/ludoMatches/"+matchId;
            }
            else{
                dicesToCheck[diceIndex]=0;
                pls.setHasTurn(0);
                nextInGameStats.setHasTurn(1);
                return "redirect:/ludoMatches/"+matchId;
            }
        } else {
            return "redirect:/";
        }
    }

    public boolean checkDicesLeft(){
        return dicesToCheck[INDICE_PRIMER_DADO]>0 || dicesToCheck[INDICE_SEGUNDO_DADO]>0;
    }


}
