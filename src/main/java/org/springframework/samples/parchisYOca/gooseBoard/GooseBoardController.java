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

    public static final int INDEX_FIRST_DICE = 0;
    public static final int INDEX_SECOND_DICE = 1;
    public static final int INDEX_SUM_OF_BOTH = 2;

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
                        boolean flagDobles = rolledDices[INDEX_FIRST_DICE] == rolledDices[INDEX_SECOND_DICE];
                        Triple<Integer,Integer,String> dicesResult = gooseChipService.checkSpecials(authenticatedUser.getUsername(),
                            loggedPlayerChip, rolledDices[INDEX_SUM_OF_BOTH], flagDobles);
                        inGamePlayerStats.setHasTurn(dicesResult.getSecond());

                        //Checks turn
                        if(dicesResult.getSecond() != 1){

                            //Stats of next player
                            Integer nextInGameId = (inGameId+1)%numberOfPlayers;
                            PlayerGooseStats nextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextInGameId, matchId).get();
                            Integer nextNextInGameId = (inGameId+2)%numberOfPlayers;
                            PlayerGooseStats nextNextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextNextInGameId, matchId).get();
                            Integer nextNextNextInGameId = (inGameId+3)%numberOfPlayers;
                            PlayerGooseStats nextNextNextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextNextNextInGameId, matchId).get();

                            if(nextInGameStats.getHasTurn() == 0){  //Gives turn to the next
                                nextInGameStats.setHasTurn(1);
                            } else if(nextInGameStats.getHasTurn() != 1){    //Skips and gives turn to the next in the losing turn square
                                nextInGameStats.setHasTurn(nextInGameStats.getHasTurn()+1);
                                nextNextInGameStats.setHasTurn(nextNextInGameStats.getHasTurn()+1);
                                if(nextNextInGameStats.getHasTurn() != 1){   //If the next loses turn too, gives to the next
                                    nextNextNextInGameStats.setHasTurn(nextNextNextInGameStats.getHasTurn()+1);
                                    if(nextNextNextInGameStats.getHasTurn() != 1){ //If everyone are losing turn, gives turn back to the first
                                        inGamePlayerStats.setHasTurn(1);
                                    }
                                }
                            }

                            playerGooseStatsService.saveStats(inGamePlayerStats);
                            playerGooseStatsService.saveStats(nextInGameStats);
                            playerGooseStatsService.saveStats(nextNextInGameStats);
                            playerGooseStatsService.saveStats(nextNextNextInGameStats);
                        }

                        //Checks special square
                        if (dicesResult.getThird() == "Bridge" || dicesResult.getThird() == "Goose"
                            ||dicesResult.getThird() == "Dice"){
                            session.setAttribute("especial", "You have landed on the special square " + dicesResult.getThird().toLowerCase(Locale.ROOT)+ ", \n"
                                +"you have been moved from square " + String.valueOf(loggedPlayerChip.getPosition()+rolledDices[INDEX_SUM_OF_BOTH]) + " to the square "+dicesResult.getFirst()
                                +". You have an extra turn!");

                        } else if(dicesResult.getThird() == "Jail" || dicesResult.getThird() == "Inn"){
                            session.setAttribute("especial", "You have landed on the special square " + dicesResult.getThird().toLowerCase(Locale.ROOT)+ ", \n"
                                +"you loose " + Math.abs(dicesResult.getSecond()) + " turns :(");
                        } else if(dicesResult.getThird() == "Maze" || dicesResult.getThird() == "Death"){
                            session.setAttribute("especial", "You have landed on the special square " + dicesResult.getThird().toLowerCase(Locale.ROOT)+ ", \n"
                                + "you have been moved to the square "+dicesResult.getFirst()+ ". Today it's not your lucky day ¯\\('-')_/¯");
                        } else if(dicesResult.getThird() == "Double roll"){
                            session.setAttribute("especial","You have landed on the square " +dicesResult.getFirst() +" and you got a double roll!! You can roll the dice again");
                        } else{
                            session.setAttribute("especial", "You moved from the square "+ loggedPlayerChip.getPosition()+ " to the square " + dicesResult.getFirst());
                        }


                        loggedPlayerChip.setPosition(dicesResult.getFirst());
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
