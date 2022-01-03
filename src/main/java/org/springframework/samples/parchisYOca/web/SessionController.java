package org.springframework.samples.parchisYOca.web;

import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;

@Controller
public class SessionController {

    public static Integer NUM_DICES=2;
    public static Integer NUM_DICES_SIDES=6;
    private final UserService userService;
    private final PlayerGooseStatsService playerGooseStatsService;
    private final PlayerLudoStatsService playerLudoStatsService;

    public SessionController(UserService userService,PlayerGooseStatsService playerGooseStatsService,PlayerLudoStatsService playerLudoStatsService) {
        this.userService = userService;
        this.playerGooseStatsService=playerGooseStatsService;
        this.playerLudoStatsService=playerLudoStatsService;
    }

    //Array where the first NUM_DICES indexes are the dices and the NUM_DICES+1 the sum of both
    @GetMapping("session/rolldices")
    public String rollDices(HttpSession session, HttpServletRequest request){

        //Check if the player has the turn
        Integer matchId = (Integer) session.getAttribute("matchId");
        int[] rolledDices = (int[])session.getAttribute("dices");
        Boolean logged = userService.isAuthenticated();
        if(logged==true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            Object refererGoose = request.getSession().getAttribute("fromGoose");
            Object refererLudo = request.getSession().getAttribute("fromLudo");
            //To avoid problems when playing only one of the games(default case)
            Integer turnGoose=0;
            Integer turnLudo=0;
            if(refererGoose != null){
                turnGoose=playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(),matchId).get().getHasTurn();
            }
            if(refererLudo != null){
                turnLudo=playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(),matchId).get().getHasTurn();
            }

            if(turnGoose==1||turnLudo==1){
                int[] dices = new int[NUM_DICES+1];
                for (Integer i = 0; i<NUM_DICES; i++){
                    dices[i] = 1+(int)Math.floor(Math.random()*NUM_DICES_SIDES);
                }
                dices[NUM_DICES] = Arrays.stream(dices).sum();
                session.setAttribute("dices", dices);
                if(refererGoose != null){
                    return "redirect:/gooseInGame/dicesRolled";
                }
                if(refererLudo != null){
                    return "redirect:/ludoMatches/"+matchId;
                }

            }
            //Redirigir a partida
            if(refererGoose != null){
                return "redirect:/gooseMatches/"+matchId;
            }
            if(refererLudo != null){
                return "redirect:/ludoMatches/"+matchId;
            }
        }
        return "redirect:/";
    }
}
