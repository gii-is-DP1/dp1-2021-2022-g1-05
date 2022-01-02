package org.springframework.samples.parchisYOca.ludoBoard;

import org.hibernate.envers.internal.tools.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class LudoBoardController {

    private final UserService userService;
    private final PlayerLudoStatsService playerLudoStatsService;
    private final LudoChipService ludoChipService;

    public static final int INDICE_PRIMER_DADO = 0;
    public static final int INDICE_SEGUNDO_DADO = 1;
    public static final int INDICE_SUMA_DADOS = 2;



    @Autowired
    public LudoBoardController(UserService userService, PlayerLudoStatsService playerLudoStatsService, LudoChipService ludoChipService){
        this.userService=userService;
        this.playerLudoStatsService = playerLudoStatsService;
        this.ludoChipService = ludoChipService;
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

            for(LudoChip lc : ludoChips){
                Integer inGameId = inGamePlayerStats.getInGameId();
                //To check that the chip belongs to the player
                if(lc.getInGamePlayerId() == inGameId){
                    LudoChip loggedPlayerChip = lc;
                    boolean flagDobles = rolledDices[INDICE_PRIMER_DADO] == rolledDices[INDICE_SEGUNDO_DADO];

                    //Comprobar si ha sacado 5 y tiene fichas en base
                    if(rolledDices[INDICE_PRIMER_DADO] == 5 || rolledDices[INDICE_SEGUNDO_DADO] == 5 ||
                        rolledDices[INDICE_SUMA_DADOS] == 5) {
                        //TODO ESTO DEBERÍA MANEJAR TODO LO RELACIONADO CON MANEJAR EL 5
                        ludoChipService.getChipsInEarlyGame(inGameId,matchId);
                    }


                    //TODO FORMULARIO PARA ELEGIR QUE DADOS SE JUEGAN PARA QUE FICHA Y EL ORDEN, ¿CREAR CLASE NUEVA PARA GUARDAR LAS ELECCIONES DEL FORMULARIO?
                    List<Integer> listaAux=new ArrayList<>();
                    model.put("respuesta",listaAux );
                    return "matches/ludoDiceForm";












                    /*
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
                    /*
                     */
                }
            }

            return "redirect:/gooseMatches/"+matchId;
        }else{
            return "redirect:/";
        }
    }



}
