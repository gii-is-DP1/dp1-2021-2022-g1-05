package org.springframework.samples.parchisYOca.gooseBoard;

import org.hibernate.envers.internal.tools.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Controller
public class GooseBoardController {

    public static final int INDICE_PRIMER_DADO = 0;
    public static final int INDICE_SEGUNDO_DADO = 1;
    public static final int INDICE_SUMA_DADOS = 2;

    private final GooseMatchService gooseMatchService;
    private final PlayerService playerService;
    private final PlayerGooseStatsService playerGooseStatsService;
    private final GooseBoardService gooseBoardService;
    private final GooseChipService gooseChipService;

    @Autowired
    public GooseBoardController(GooseMatchService gooseMatchService, PlayerService playerService,
                                PlayerGooseStatsService playerGooseStatsService,
                                GooseBoardService gooseBoardService, GooseChipService gooseChipService){
        this.gooseMatchService = gooseMatchService;
        this.playerService = playerService;
        this.playerGooseStatsService = playerGooseStatsService;
        this.gooseBoardService = gooseBoardService;
        this.gooseChipService = gooseChipService;
    }



    @GetMapping(value = "/gooseInGame/dicesRolled")
    public String gooseDicesRolled(HttpSession session){
        Integer matchId = (Integer) session.getAttribute("matchId");
        int[] rolledDices = (int[])session.getAttribute("dices");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

        PlayerGooseStats inGamePlayerStats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(
            authenticatedUser.getUsername(), matchId);
        Set<GooseChip> gooseChips = new HashSet<>(gooseChipService.findChipsByMatchId(matchId));
        Integer numberOfPlayers = gooseChips.size();

        for(GooseChip gc : gooseChips){
            Integer inGameId = inGamePlayerStats.getInGameId();
            if(gc.getInGameId() == inGameId){
                GooseChip loggedPlayerChip = gc;
                boolean flagDobles = rolledDices[INDICE_PRIMER_DADO] == rolledDices[INDICE_SEGUNDO_DADO];
                Triple<Integer,Integer,String> resultadoTirada = gooseChipService.checkSpecials(
                    loggedPlayerChip, rolledDices[INDICE_SUMA_DADOS], flagDobles);
                inGamePlayerStats.setHasTurn(resultadoTirada.getSecond());

              //Comprobación del turno
                if(resultadoTirada.getSecond() != 1){
                    //Estadisticas del siguiente jugador
                    Integer nextInGameId = (inGameId+1)%numberOfPlayers;
                    PlayerGooseStats nextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextInGameId, matchId);

                    if(nextInGameStats.getHasTurn() == 0){
                        nextInGameStats.setHasTurn(1);
                    } else if(nextInGameStats.getHasTurn() < 0){
                        inGamePlayerStats.setHasTurn(inGamePlayerStats.getHasTurn()+1);
                        nextInGameStats.setHasTurn(nextInGameStats.getHasTurn()+1);
                    }
                    playerGooseStatsService.saveStats(nextInGameStats);
                }

                //Comprobación de casilla especial
                if (resultadoTirada.getThird() == "Bridge" || resultadoTirada.getThird() == "Goose"
                ||resultadoTirada.getThird() == "Dice"){
                    session.setAttribute("especial", "You have landed on the special square " + resultadoTirada.getThird().toLowerCase(Locale.ROOT)+ ", \n"
                    +"you have been moved from square " + String.valueOf(loggedPlayerChip.getPosition()+rolledDices[INDICE_SUMA_DADOS]) + " to the square "+resultadoTirada.getFirst()
                    +". You have an extra turn!");

                } else if(resultadoTirada.getThird() == "Jail" || resultadoTirada.getThird() == "Inn"){
                    session.setAttribute("especial", "You have landed on the special square " + resultadoTirada.getThird().toLowerCase(Locale.ROOT)+ ", \n"
                        +"you loose " + Math.abs(resultadoTirada.getSecond()) + " turns :(");
                } else if(resultadoTirada.getThird() == "Maze" || resultadoTirada.getThird() == "Death"){
                    session.setAttribute("especial", "You have landed on the special square " + resultadoTirada.getThird().toLowerCase(Locale.ROOT)+ ", \n"
                        + "you have been moved to the square "+resultadoTirada.getFirst()+ ". Today it's not your lucky day ¯\\('-')_/¯");
                } else if(resultadoTirada.getThird() == "Double roll"){
                    session.setAttribute("especial","You got a double roll!! You can roll the dice again");
                } else{
                    session.setAttribute("especial", "You moved from the square "+ loggedPlayerChip.getPosition()+ " to the square " + resultadoTirada.getFirst());
                }


                loggedPlayerChip.setPosition(resultadoTirada.getFirst());
                gooseChipService.save(loggedPlayerChip);
                playerGooseStatsService.saveStats(inGamePlayerStats);
            }
        }

        return "redirect:/gooseMatches/"+matchId;
    }

     /*@GetMapping(value = "/gooseInGame/turnLost")
    public String gooseTurnLost(HttpSession session) {
        Integer matchId = (Integer) session.getAttribute("matchId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

        PlayerGooseStats inGamePlayerStats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(
            authenticatedUser.getUsername(), matchId);
        Set<GooseChip> gooseChips = new HashSet<>(gooseChipService.findChipsByMatchId(matchId));
        Integer numberOfPlayers = gooseChips.size();

        for(GooseChip gc : gooseChips) {
            Integer inGameId = inGamePlayerStats.getInGameId();
            if (gc.getInGameId() == inGameId) {
                inGamePlayerStats.setHasTurn(inGamePlayerStats.getHasTurn()+1);
                //Estadisticas del siguiente jugador
                Integer nextInGameId = (inGameId + 1) % numberOfPlayers;
                PlayerGooseStats nextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextInGameId, matchId);

                if(nextInGameStats.getHasTurn() == 0){
                    nextInGameStats.setHasTurn(1);
                } else{
                    nextInGameStats.setHasTurn(nextInGameStats.getHasTurn() + 1);
                }

                playerGooseStatsService.saveStats(nextInGameStats);
            }
        }
        return "redirect:/gooseMatches/"+matchId;
    }*/
}
