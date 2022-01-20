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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LudoBoardController {

    private final UserService userService;
    private final PlayerLudoStatsService playerLudoStatsService;
    private final LudoChipService ludoChipService;
    private final LudoMatchService ludoMatchService;
    private final LudoBoardService ludoBoardService;
    public static final Integer INDEX_FIRST_DICE = 0;
    public static final Integer INDEX_SECOND_DICE = 1;
    public static final Integer INDEX_SUM_OF_BOTH = 2;
    public static final Integer FIRST_DICE_5=0;
    public static final Integer SECOND_DICE_5=1;
    public static final Integer SUM_DICES_5=2;
    public static final Integer TWO_DICES_5=3;
    private static final String ALL_CHIPS_OF_A_PLAYER ="0123";
    private int[] dicesToCheck = new int[3];

    public static final Integer ATE_CHIP=1;
    public static final Integer LANDED_FINAL=2;
    public static final Integer GOT_BLOCKED=3;
    public static final Integer BLOCKED_AND_ATE=4;
    public static final Integer ENDED_THE_GAME=5;


    @Autowired
    public LudoBoardController(UserService userService, PlayerLudoStatsService playerLudoStatsService, LudoChipService ludoChipService,
                               LudoMatchService ludoMatchService, LudoBoardService ludoBoardService){
        this.userService=userService;
        this.playerLudoStatsService = playerLudoStatsService;
        this.ludoChipService = ludoChipService;
        this.ludoMatchService = ludoMatchService;
        this.ludoBoardService = ludoBoardService;
    }

    public void populateModel(Map<String, Object> model, Integer matchId, int[] dicesToShow) {
        LudoMatch match = ludoMatchService.findludoMatchById(matchId).get();
        model.put("stats", match.getStats());
        model.put("chips", ludoChipService.findChipsByMatchId(matchId));
        model.put("firstDice", dicesToShow[INDEX_FIRST_DICE]);
        model.put("secondDice", dicesToShow[INDEX_SECOND_DICE]);
        model.put("sumDice", dicesToShow[INDEX_SUM_OF_BOTH]);
        model.put("ludoBoard", ludoMatchService.findludoMatchById(match.getId()).get().getBoard());
    }

    @GetMapping(value="/ludoInGame/dicesRolled")
    public String ludoDicesRolled(HttpSession session, ModelMap model){
        Boolean logged = userService.isAuthenticated();
        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Integer matchId = (Integer) session.getAttribute("matchId");
            int[] dicesToShow = (int[])session.getAttribute("dices");
            List<LudoChip> ludoChips = new ArrayList<>(ludoChipService.findChipsByMatchId(matchId));
            PlayerLudoStats inGamePlayerStats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(
                authenticatedUser.getUsername(), matchId).get();
            model.put("chipsToBeDisplaced", ludoChipService.checkOcuppied(ludoChips));
            //putting things in the model
            model.put("dicesRolled", 1);
            populateModel(model, matchId, dicesToShow);
            dicesToCheck[INDEX_FIRST_DICE] = dicesToShow[INDEX_FIRST_DICE];
            dicesToCheck[INDEX_SECOND_DICE] = dicesToShow[INDEX_SECOND_DICE];
            dicesToCheck[INDEX_SUM_OF_BOTH] = dicesToShow[INDEX_SUM_OF_BOTH];
            boolean flagDobles = dicesToCheck[INDEX_FIRST_DICE] == dicesToCheck[INDEX_SECOND_DICE];
            session.setAttribute("flagDobles", flagDobles);
            Integer inGameId = inGamePlayerStats.getInGameId();
            session.setAttribute("especial", null);

            //Checks if this is the third time in a turn they roll double dices
            if(ludoBoardService.checkGreedy(inGamePlayerStats,flagDobles)){
                ludoChipService.manageGreedy(matchId, inGamePlayerStats);
                session.setAttribute("especial", "You managed to roll doubles THREE TIMES? Preposterous, go back home.");
                passTurn(inGamePlayerStats, matchId);
                return "redirect:/ludoMatches/" + matchId;
            }
            for(LudoChip lc : ludoChips){
                //To check that the chip belongs to the player
                if(lc.getInGamePlayerId() == inGameId){
                    LudoChip loggedPlayerChip = lc;

                    //Comprobar si ha sacado 5 y tiene fichas en base
                    if(dicesToCheck[INDEX_FIRST_DICE] == 5 || dicesToCheck[INDEX_SECOND_DICE] == 5 ||
                        dicesToCheck[INDEX_SUM_OF_BOTH] == 5) {
                        Integer diceCode = ludoChipService.manageFives(inGameId,matchId, dicesToCheck[INDEX_FIRST_DICE], dicesToCheck[INDEX_SECOND_DICE]);
                        model.put("diceCode", diceCode);
                        responseToFives(diceCode, inGamePlayerStats, matchId, model, session);
                        if(diceCode == SUM_DICES_5 || diceCode == TWO_DICES_5) {
                            return "redirect:/ludoMatches/" + matchId;
                        }
                    }
                }
            }
            //Si todas las fichas en casa no puedes mover, tu turno ha terminado
            if(ludoChipService.noChipsOutOfHome(ludoChips,inGameId)){
                if(!flagDobles) {
                    dicesToCheck[INDEX_FIRST_DICE] = 0 ; dicesToCheck[INDEX_SECOND_DICE]=0;
                    passTurn(inGamePlayerStats, matchId);
                    session.setAttribute("especial", "You weren't able to take out any of your chips :(");
                } else {
                    session.setAttribute("especial", "You couldn't take out any chips, but you rolled double, sou you get an extra turn!");
                }
                return "redirect:/ludoMatches/" + matchId;
            }
            //Comprueba los bloqueos, fuerza a romperlos si has sacado dobles
            if(flagDobles) {
                List<LudoChip>  chipsToBreak = ludoChipService.breakBlocks(ludoChips, inGameId);
                if(chipsToBreak.size() != 0) {
                    return "redirect:/ludoInGame/chooseChip/0";
                } else {
                    model.addAttribute("message","You got a double roll!! You will be able to roll again after this.");
                    session.setAttribute("especial","You got a double roll!! You can roll the dice again.");
                }
            }
            //Ya no te quedan movimientos, tu turno ha terminado
            if(!checkDicesLeft()) {
                passTurn(inGamePlayerStats, matchId);
                return "redirect:/ludoMatches/" + matchId;
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
            List<LudoChip> ludoChips = new ArrayList<>(ludoChipService.findChipsByMatchId(matchId));
            model.put("chipsToBeDisplaced", ludoChipService.checkOcuppied(ludoChips));
            PlayerLudoStats inGamePlayerStats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(
                authenticatedUser.getUsername(), matchId).get();

            Boolean flagDobles = (Boolean) session.getAttribute("flagDobles");
            List<LudoChip>  chipsToBreak = new ArrayList<>();
            if(flagDobles) {
                chipsToBreak=ludoChipService.breakBlocks(ludoChips,inGamePlayerStats.getInGameId());
            }
            if(dicesToCheck[diceIndex]==20){
                model.addAttribute("message","You ate a chip so you get another free 20 movements, on the house.");
            }
            if(dicesToCheck[diceIndex]==10){
                model.addAttribute("message","You scored one of your chips so you get another free 10 moves!");
            }
            if(chipsToBreak.size() != 0) {
                model.put("breakBlock", true);
                model.put("message", "You got doubles so you must break a block!");
                String chipsToMove="";
                for(LudoChip chip:chipsToBreak){
                    chipsToMove+=chip.getInGameChipId();
                }
                model.put("chipsToMove",chipsToMove);
            }
            else{
                model.put("chipsToMove", ALL_CHIPS_OF_A_PLAYER);
            }
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
            List<LudoChip> chips =new ArrayList<>(ludoChipService.findChipsByMatchId(matchId));
            model.put("chipsToBeDisplaced", ludoChipService.checkOcuppied(chips));
            LudoChip chip=ludoChipService.findConcreteChip(matchId,inGameChipId,inGamePlayerId).get();
            Integer moveCode = ludoChipService.move(chip,dicesToCheck[diceIndex],chips,pls, matchId);
            dicesToCheck[diceIndex]=0;

            if(moveCode==ATE_CHIP || moveCode==BLOCKED_AND_ATE){
                dicesToCheck[diceIndex]=20;
                return "redirect:/ludoInGame/chooseChip/"+diceIndex;
            }else if(moveCode==LANDED_FINAL){
                dicesToCheck[diceIndex]=10;
                return "redirect:/ludoInGame/chooseChip/"+diceIndex;
            }else if(moveCode==GOT_BLOCKED) {
                session.setAttribute("especial", "You got blocked, so your chip landed right before the block");
            }else if(moveCode==ENDED_THE_GAME) {
                return "redirect:/ludoMatches/"+matchId;
            }
            if(checkDicesLeft()){
                return "redirect:/ludoInGame/chooseChip/"+Integer.toString((diceIndex+1)%2);
            }
            else if((boolean) session.getAttribute("flagDobles")){
                session.setAttribute("especial","You got a double roll!! You can roll the dice again");
                return "redirect:/ludoMatches/"+matchId;
            }
            else{
                passTurn(pls, matchId);
                return "redirect:/ludoMatches/"+matchId;
            }
        }
        return "redirect:/";
    }

    private boolean checkDicesLeft(){
        return dicesToCheck[INDEX_FIRST_DICE]>0 || dicesToCheck[INDEX_SECOND_DICE]>0;
    }

    //Resetea los dados usados y muestra los mensajes pertinentes
    private void responseToFives(Integer diceCode, PlayerLudoStats inGamePlayerStats, Integer matchId, ModelMap model, HttpSession session) {
        if(diceCode==FIRST_DICE_5){
            dicesToCheck[INDEX_FIRST_DICE]=0;
            model.addAttribute("message", "You rolled  5, so one of your chips got taken out");
        }
        else if(diceCode==SECOND_DICE_5){
            dicesToCheck[INDEX_SECOND_DICE]=0;
            model.addAttribute("message", "You rolled  5, so one of your chips got taken out");
        }
        else if(diceCode==SUM_DICES_5){
            dicesToCheck[INDEX_FIRST_DICE] = 0 ; dicesToCheck[INDEX_SECOND_DICE]=0;
            passTurn(inGamePlayerStats, matchId);
            session.setAttribute("especial", "Your roll sums 5, so one of your chips got taken out");
        } else if(diceCode == TWO_DICES_5){
            dicesToCheck[INDEX_FIRST_DICE] = 0 ; dicesToCheck[INDEX_SECOND_DICE]=0;
            session.setAttribute("especial", "You rolled 5 on both dices, so two of your chips got taken out");
        }
    }

    private void passTurn(PlayerLudoStats inGamePlayerStats, Integer matchId) {
        Integer numberOfPlayers = ludoChipService.findChipsByMatchId(matchId).size()/4;
        Integer nextInGameId = (inGamePlayerStats.getInGameId()+1)%numberOfPlayers;
        PlayerLudoStats nextInGameStats = playerLudoStatsService.findPlayerLudoStatsByInGameIdAndMatchId(nextInGameId, matchId).get();
        nextInGameStats.setHasTurn(1);
        inGamePlayerStats.setHasTurn(0);
        inGamePlayerStats.setTurnDoubleRolls(0);
        playerLudoStatsService.saveStats(inGamePlayerStats);
        playerLudoStatsService.saveStats(nextInGameStats);
    }


}
