package org.springframework.samples.parchisYOca.gooseBoard;

import org.hibernate.envers.internal.tools.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.gooseChip.exceptions.InvalidChipPositionException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Controller
public class GooseBoardController {

    public static final int INDICE_PRIMER_DADO = 0;
    public static final int INDICE_SEGUNDO_DADO = 1;
    public static final int INDICE_SUMA_DADOS = 2;

    private final PlayerGooseStatsService playerGooseStatsService;
    private final GooseChipService gooseChipService;
    private final UserService userService;

    @Autowired
    public GooseBoardController(PlayerGooseStatsService playerGooseStatsService, GooseChipService gooseChipService, UserService userService){
        this.playerGooseStatsService = playerGooseStatsService;
        this.gooseChipService = gooseChipService;
        this.userService = userService;
    }



    @GetMapping(value = "/gooseInGame/dicesRolled")
    public String gooseDicesRolled(HttpSession session) throws InvalidChipPositionException {
        Boolean logged = userService.isAuthenticated();

        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            Integer matchId = (Integer) session.getAttribute("matchId");
            int[] rolledDices = (int[])session.getAttribute("dices");

            PlayerGooseStats inGamePlayerStats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(
                authenticatedUser.getUsername(), matchId).get();
            Integer hasTurn = inGamePlayerStats.getHasTurn();
            if(hasTurn==1) {
            	Set<GooseChip> gooseChips = new HashSet<>(gooseChipService.findChipsByMatchId(matchId));
                Integer numberOfPlayers = gooseChips.size();

                for(GooseChip gc : gooseChips){
                    Integer inGameId = inGamePlayerStats.getInGameId();
                    if(gc.getInGameId() == inGameId){
                        GooseChip loggedPlayerChip = gc;
                        boolean flagDobles = rolledDices[INDICE_PRIMER_DADO] == rolledDices[INDICE_SEGUNDO_DADO];
                        Triple<Integer,Integer,String> resultadoTirada = gooseChipService.checkSpecials(authenticatedUser.getUsername(),
                            loggedPlayerChip, rolledDices[INDICE_SUMA_DADOS], flagDobles);
                        inGamePlayerStats.setHasTurn(resultadoTirada.getSecond());

                        //Comprobación del turno
                        if(resultadoTirada.getSecond() != 1){

                            //Estadisticas del siguiente jugador
                            Integer nextInGameId = (inGameId+1)%numberOfPlayers;
                            PlayerGooseStats nextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextInGameId, matchId).get();
                            Integer nextNextInGameId = (inGameId+2)%numberOfPlayers;
                            PlayerGooseStats nextNextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextNextInGameId, matchId).get();
                            Integer nextNextNextInGameId = (inGameId+3)%numberOfPlayers;
                            PlayerGooseStats nextNextNextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextNextNextInGameId, matchId).get();

                            if(nextInGameStats.getHasTurn() == 0){  //Le da el turno al siguiente
                                nextInGameStats.setHasTurn(1);
                            } else if(nextInGameStats.getHasTurn() != 1){    //Le pasa y le da el turno al siguiente que está en la casilla de perdida de turnos
                                nextInGameStats.setHasTurn(nextInGameStats.getHasTurn()+1);
                                nextNextInGameStats.setHasTurn(nextNextInGameStats.getHasTurn()+1);
                                if(nextNextInGameStats.getHasTurn() != 1){   //Si el siguiente al siguiente tambien esta en una casilla de este tipo, le pasa al que va a continuación
                                    nextNextNextInGameStats.setHasTurn(nextNextNextInGameStats.getHasTurn()+1);
                                    if(nextNextNextInGameStats.getHasTurn() != 1){ //Si todos están perdiendo el turno, vuelve al comienzo
                                        inGamePlayerStats.setHasTurn(1);
                                    }
                                }
                            }

                            playerGooseStatsService.saveStats(inGamePlayerStats);
                            playerGooseStatsService.saveStats(nextInGameStats);
                            playerGooseStatsService.saveStats(nextNextInGameStats);
                            playerGooseStatsService.saveStats(nextNextNextInGameStats);
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
                            session.setAttribute("especial","You have landed on the square " +resultadoTirada.getFirst() +" and you got a double roll!! You can roll the dice again");
                        } else{
                            session.setAttribute("especial", "You moved from the square "+ loggedPlayerChip.getPosition()+ " to the square " + resultadoTirada.getFirst());
                        }


                        loggedPlayerChip.setPosition(resultadoTirada.getFirst());
                        gooseChipService.save(loggedPlayerChip);
                        playerGooseStatsService.saveStats(inGamePlayerStats);
                    }
                }

                return "redirect:/gooseMatches/"+matchId;
            } else {
            	session.setAttribute("especial", "Don't try that silly boy");
            	return "redirect:/gooseMatches/"+matchId;
            }

        }else{
            return "redirect:/";
        }


    }

}
