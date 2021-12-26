package org.springframework.samples.parchisYOca.gooseMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardService;
import org.springframework.samples.parchisYOca.gooseChip.GooseChip;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.user.AuthoritiesService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.samples.parchisYOca.util.RandomStringGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.*;

@Controller
public class GooseMatchController {

    private final GooseMatchService gooseMatchService;
    private final PlayerService playerService;
    private final PlayerGooseStatsService playerGooseStatsService;
    private final GooseBoardService gooseBoardService;
    private final GooseChipService gooseChipService;

    private static final Integer MATCH_CODE_LENGTH = 6;
    private static final Integer MAX_NUMBER_OF_PLAYERS = 4;
    private static final Integer INDEX_OF_LOBBY = 0;

    @Autowired
    public GooseMatchController(GooseMatchService gooseMatchService, PlayerService playerService,
                                PlayerGooseStatsService playerGooseStatsService,
                                GooseBoardService gooseBoardService, GooseChipService gooseChipService){
        this.gooseMatchService = gooseMatchService;
        this.playerService = playerService;
        this.playerGooseStatsService = playerGooseStatsService;
        this.gooseBoardService = gooseBoardService;
        this.gooseChipService = gooseChipService;
    }

    @GetMapping("/gooseMatches/new")
    public String createGooseMatch(ModelMap modelMap){
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        GooseMatch gooseMatch = new GooseMatch();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();

        List<GooseMatch> playerInGooseMatches = new ArrayList<>(gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()));
        if(playerInGooseMatches.size() != INDEX_OF_LOBBY){
            GooseMatch playerInGooseMatch = playerInGooseMatches.get(INDEX_OF_LOBBY);
            modelMap.addAttribute("message", "You are already at a lobby: "+playerInGooseMatch.getMatchCode());
            return "redirect:/";
        }

        gooseMatch.setMatchCode(matchCode);
        gooseMatchService.saveGooseMatchWithPlayer(gooseMatch, player, true);
        return "redirect:/gooseMatches/lobby/"+matchCode;
    }

    @GetMapping(value = "/gooseMatches/join")
    public String  joinGooseMatchForm(Map<String, Object> model){
        GooseMatch match = new GooseMatch();
        model.put("match", match);
        return "matches/joinMatchForm";
    }

    @PostMapping(value = "/gooseMatches/join")
    public String joinGooseMatch(@RequestParam String matchCode, ModelMap modelMap){
        Optional<GooseMatch> gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
        Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();

        List<GooseMatch> playerInGooseMatches = new ArrayList<>(gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()));


        if(playerInGooseMatches.size()!=INDEX_OF_LOBBY){ //If player is in a match
            if(matchCode.equals(playerInGooseMatches.get(INDEX_OF_LOBBY).getMatchCode())){   //If the player enters the lobby they are in
                return "redirect:/gooseMatches/lobby/" + matchCode;
            }else{
                modelMap.addAttribute("message", "You are already at a lobby: "+playerInGooseMatches.get(INDEX_OF_LOBBY).getMatchCode());
            }
        } else{
            if(gooseMatch.isPresent()) { //If the game exists
                if(!(gooseMatch.get().getStats().size()>=MAX_NUMBER_OF_PLAYERS)) { //If the game is not full
                    gooseMatchService.saveGooseMatchWithPlayer(gooseMatch.get(),player, false);
                    return "redirect:/gooseMatches/lobby/"+matchCode;
                }else{
                    modelMap.addAttribute("message", "The lobby is full!");
                }
            }else{
                modelMap.addAttribute("message", "Lobby not found!");
            }
        }
        return "matches/joinMatchForm";
    }


    @GetMapping(value = "/gooseMatches/lobby/{matchCode}")
    public String initCreationLobby(@PathVariable("matchCode") String matchCode, ModelMap modelMap, HttpServletResponse response) {
        //If the game started
        if(gooseMatchService.findGooseMatchByMatchCode(matchCode).get().getStartDate() != null){
            return "redirect:/gooseMatches/"+gooseMatchService.findGooseMatchByMatchCode(matchCode).get().getId();
        }

        response.addHeader("Refresh", "5");

        GooseMatch gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode).get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

        modelMap.addAttribute("numberOfPlayers", gooseMatch.getStats().size());
        modelMap.addAttribute("isOwner", playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(), gooseMatch.getId()).getIsOwner());
        modelMap.addAttribute("stats", gooseMatch.getStats());
        modelMap.addAttribute("matchCode", matchCode);
        modelMap.addAttribute("match", gooseMatch);
        modelMap.addAttribute("matchId",gooseMatch.getId());

        return "matches/gooseMatchLobby";
    }


    @GetMapping(value = "/gooseMatches/{matchId}")
    public String showMatch(@PathVariable("matchId") Integer matchId, ModelMap model,
                            HttpServletRequest request, HttpSession session, HttpServletResponse response){
        response.addHeader("Refresh", "10");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

        //To be able to redirect back when rolling the dice
        request.getSession().setAttribute("fromGoose", true);
        request.getSession().setAttribute("matchId", matchId);


        String view = "matches/gooseMatch";
        GooseMatch match = gooseMatchService.findGooseMatchById(matchId).get();
        model.put("stats", match.getStats());

        //If the match is a new one, sets the start date and creates the board with its chips
        if (match.getStartDate() == null){
            match.setStartDate(new Date());
            GooseBoard board = new GooseBoard();
            GooseBoard savedBoard = gooseBoardService.save(board, match.getStats().size());
            match.setBoard(savedBoard);
        }
        gooseMatchService.save(match);
        model.put("chips",gooseChipService.findChipsByMatchId(matchId));


        if (session.getAttribute("dices") != null){
            int[] dices = (int[])session.getAttribute("dices");
            model.put("firstDice", dices[0]);
            model.put("secondDice", dices[1]);
            model.put("sumDice", dices[2]);
        }


        PlayerGooseStats stats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(), matchId);

        //To show the other players if their game has been closed or has ended
        if(gooseMatchService.findGooseMatchById(matchId).get().getEndDate() != null){
            model.addAttribute("message", "The game has ended!");
        }else{
            //To show if they landed on a special square
            if (session.getAttribute("especial") != null){
                String mensaje = session.getAttribute("especial").toString();
                model.put("message", mensaje);
            }
            if(!(stats.getHasTurn() < 0)){
                Integer hasTurn = stats.getHasTurn();
                model.put("hasTurn", hasTurn);
            }
        }
        model.put("gooseBoard", gooseMatchService.findGooseMatchById(match.getId()).get().getBoard());

        return view;
    }

    @GetMapping(value="/gooseMatches")
    public String listadoPartidas(ModelMap modelMap){
        String vista = "matches/listGooseMatches";
        Iterable<GooseMatch> gooseMatches = gooseMatchService.findAll();
        modelMap.addAttribute("gooseMatches",gooseMatches);
        return vista;
    }

    @GetMapping(value="/gooseMatches/close/{matchId}")
    public String closeMatch(@PathVariable("matchId") Integer matchId){
        GooseMatch gooseMatchDb=gooseMatchService.findGooseMatchById(matchId).get();
        gooseMatchDb.setEndDate(new Date());
        gooseMatchService.save(gooseMatchDb);
        return "redirect:/gooseMatches";

    }

}
