package org.springframework.samples.parchisYOca.gooseBoard;

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
import java.util.Map;
import java.util.Set;

@Controller
public class GooseBoardController {

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
    public String gooseDicesRolled(Map<String, Object> model, HttpSession session){
        Integer matchId = (Integer) session.getAttribute("matchId");
        int[] rolledDices = (int[])session.getAttribute("dices");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

        PlayerGooseStats inGamePlayerStats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(
            authenticatedUser.getUsername(), matchId);
        Set<GooseChip> gooseChips = new HashSet<>(gooseChipService.findChipsByMatchId(matchId));
        Integer numberOfPlayers = gooseChips.size();
        GooseChip loggedPlayerChip;

        for(GooseChip gc : gooseChips){
            Integer inGameId = inGamePlayerStats.getInGameId();
            if(gc.getInGameId() == inGameId){
                loggedPlayerChip = gc;
                loggedPlayerChip.setPosition(loggedPlayerChip.getPosition() + rolledDices[2]);

                 //Para ver dobles
               /* if(rolledDices[0] != rolledDices[1]){
                    inGamePlayerStats.setHasTurn(0);
                    Integer nextInGameId = (inGameId+1)%numberOfPlayers;
                    PlayerGooseStats nextInGameStats = playerGooseStatsService.findPlayerGooseStatsByInGameIdAndMatchId(nextInGameId, matchId);

                    if(nextInGameStats.getHasTurn() < 0){
                        nextInGameStats.setHasTurn(nextInGameStats.getHasTurn()+1);
                    }else{
                        nextInGameStats.setHasTurn(1);
                    }
                    playerGooseStatsService.saveStats(nextInGameStats);
                }else{
                    inGamePlayerStats.setDoubleRolls(inGamePlayerStats.getDoubleRolls() + 1);

                }
                */

                gooseChipService.getEspeciales(loggedPlayerChip);
                playerGooseStatsService.saveStats(inGamePlayerStats);

            }
        }

        return "redirect:/gooseMatches/"+matchId;
    }
}
