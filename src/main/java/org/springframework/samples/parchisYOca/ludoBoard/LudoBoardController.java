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



    @Autowired
    public LudoBoardController(UserService userService, PlayerLudoStatsService playerLudoStatsService, LudoChipService ludoChipService,
                                LudoMatchService ludoMatchService){
        this.userService=userService;
        this.playerLudoStatsService = playerLudoStatsService;
        this.ludoChipService = ludoChipService;
        this.ludoMatchService = ludoMatchService;
    }

    @GetMapping(value="/ludoInGame/dicesRolled")
    public String ludoDicesRolled(HttpSession session, Map<String, Object> model){
        Integer matchId = (Integer) session.getAttribute("matchId");
        int[] rolledDices = (int[])session.getAttribute("dices");
        Boolean logged = userService.isAuthenticated();

        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            PlayerLudoStats inGamePlayerStats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(
                authenticatedUser.getUsername(), matchId).get();
            Set<LudoChip> ludoChips = new HashSet<>(ludoChipService.findChipsByMatchId(matchId));
            Integer numberOfPlayers = ludoChips.size();

            //putting things in the model
            model.put("dicesRolled", 1);
            LudoMatch match = ludoMatchService.findludoMatchById(matchId).get();
            model.put("stats", match.getStats());
            model.put("chips", ludoChipService.findChipsByMatchId(matchId));
            model.put("firstDice", rolledDices[INDICE_PRIMER_DADO]);
            model.put("secondDice", rolledDices[INDICE_SEGUNDO_DADO]);
            model.put("sumDice", rolledDices[INDICE_SUMA_DADOS]);
            model.put("ludoBoard", ludoMatchService.findludoMatchById(match.getId()).get().getBoard());

            for(LudoChip lc : ludoChips){
                Integer inGameId = inGamePlayerStats.getInGameId();
                //To check that the chip belongs to the player
                if(lc.getInGamePlayerId() == inGameId){
                    LudoChip loggedPlayerChip = lc;
                    boolean flagDobles = rolledDices[INDICE_PRIMER_DADO] == rolledDices[INDICE_SEGUNDO_DADO];

                    //Comprobar si ha sacado 5 y tiene fichas en base
                    if(rolledDices[INDICE_PRIMER_DADO] == 5 || rolledDices[INDICE_SEGUNDO_DADO] == 5 ||
                        rolledDices[INDICE_SUMA_DADOS] == 5) {
                        Integer diceCode = ludoChipService.manageFives(inGameId,matchId, rolledDices[INDICE_PRIMER_DADO], rolledDices[INDICE_SEGUNDO_DADO]);
                        model.put("diceCode", diceCode);
                        if(diceCode==0){
                            rolledDices[INDICE_PRIMER_DADO]=0;
                            rolledDices[INDICE_SUMA_DADOS]=rolledDices[INDICE_SUMA_DADOS]-rolledDices[INDICE_PRIMER_DADO];
                        }
                        else if(diceCode==1){
                            rolledDices[INDICE_SEGUNDO_DADO]=0;
                            rolledDices[INDICE_SUMA_DADOS]=rolledDices[INDICE_SUMA_DADOS]-rolledDices[INDICE_SEGUNDO_DADO];
                        }
                        else if(diceCode==2){
                            rolledDices[INDICE_PRIMER_DADO]=0;
                            rolledDices[INDICE_SEGUNDO_DADO]=0;
                            rolledDices[INDICE_SUMA_DADOS]=0;

                        } else if(diceCode == 3){
                            //TODO cuando saque dos cincos tiene que no dejarte tener el turno y tirar de nuevo los dados
                            rolledDices[INDICE_PRIMER_DADO]=0;
                            rolledDices[INDICE_SEGUNDO_DADO]=0;
                            rolledDices[INDICE_SUMA_DADOS]=0;
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

    @GetMapping(value = "/ludoInGame/sumDice/{diceIndex}")
    public String ludoSumDice(@PathVariable("diceIndex") Integer diceIndex, ModelMap model,
                               HttpServletRequest request, HttpSession session) {
        Boolean logged = userService.isAuthenticated();

        if(logged==true){

            return "matches/ludoSumDice";
        } else {
            return "redirect:/";
        }

    }



}
