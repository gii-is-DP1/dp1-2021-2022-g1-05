package org.springframework.samples.parchisYOca.ludoMatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.samples.parchisYOca.achievement.AchievementService;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoardService;
import org.springframework.samples.parchisYOca.ludoChip.LudoChip;
import org.springframework.samples.parchisYOca.ludoChip.LudoChipService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
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
import java.util.*;

@Controller
public class LudoMatchController {

    private final LudoChipService ludoChipService;
    private final LudoMatchService ludoMatchService;
    private final LudoBoardService ludoBoardService;
    private final PlayerService playerService;
    private final PlayerLudoStatsService playerLudoStatsService;
    private final UserService userService;
    private final AchievementService achievementService;

    private static final Integer MATCH_CODE_LENGTH = 6;
    private static final Integer MAX_NUMBER_OF_PLAYERS = 4;
    private static final String REFRESH_RATE_LOBBY = "3";
    private static final String REFRESH_RATE_MATCH = "2";
    private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 6;

    @Autowired
    public LudoMatchController(LudoMatchService ludoMatchService, PlayerService playerService,
                               PlayerLudoStatsService playerLudoStatsService, UserService userService,
                               LudoChipService ludoChipService, AchievementService achievementService,
                               LudoBoardService ludoBoardService, AuthoritiesService authoritiesService){
        this.ludoMatchService = ludoMatchService;
        this.playerService = playerService;
        this.playerLudoStatsService = playerLudoStatsService;
        this.ludoChipService = ludoChipService;
        this.ludoBoardService = ludoBoardService;
        this.achievementService = achievementService;
        this.userService = userService;
    }

    @GetMapping("/ludoMatches/new")
    public String createMatch(ModelMap modelMap){
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        LudoMatch ludoMatch = new LudoMatch();
        Boolean logged = userService.isAuthenticated();

        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();

            Optional<LudoMatch> playerInLudoMatches =ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername());
            if(playerInLudoMatches.isPresent()){
                LudoMatch playerInLudoMatch = playerInLudoMatches.get();
                modelMap.addAttribute("message", "You are already at a lobby: "+playerInLudoMatch.getMatchCode());
                return "redirect:/";
            }

            ludoMatch.setMatchCode(matchCode);
            ludoMatchService.saveludoMatchWithPlayer(ludoMatch, player, true);
            return "redirect:/ludoMatches/lobby/"+matchCode;
        }else{
            return "redirect:/";
        }
    }

    @GetMapping(value = "/ludoMatches/join")
    public String  joinLudoMatchForm(Map<String, Object> model){
        LudoMatch match = new LudoMatch();
        model.put("match", match);
        return "matches/joinMatchForm";
    }

    @PostMapping(value = "/ludoMatches/join")
    public String joinLudoMatch(@RequestParam String matchCode, ModelMap modelMap){
        Optional<LudoMatch> ludoMatch = ludoMatchService.findludoMatchByMatchCode(matchCode);
        Boolean logged = userService.isAuthenticated();

        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();

            Optional<LudoMatch> playerInLudoMatches =ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername());


            if(playerInLudoMatches.isPresent()){ //If player is in a match
                if(matchCode.equals(playerInLudoMatches.get().getMatchCode())){   //If the player enters the lobby they are in
                    return "redirect:/ludoMatches/lobby/" + matchCode;
                }else{
                    modelMap.addAttribute("message", "You are already at a lobby: "+playerInLudoMatches.get().getMatchCode());
                }
            } else{
                if(ludoMatch.isPresent()) { //If the game exists
                    if(!(ludoMatch.get().getStats().size()>=MAX_NUMBER_OF_PLAYERS)) { //If the game is not full
                        ludoMatchService.saveludoMatchWithPlayer(ludoMatch.get(),player, false);
                        return "redirect:/ludoMatches/lobby/"+matchCode;
                    }else{
                        modelMap.addAttribute("message", "The lobby is full!");
                    }
                }else{
                    modelMap.addAttribute("message", "Lobby not found!");
                }
            }
            return "matches/joinMatchForm";
        }else{
            return "redirect:/";
        }

    }


    @GetMapping(value = "/ludoMatches/lobby/{matchCode}")
    public String initCreationLobby(@PathVariable("matchCode") String matchCode, ModelMap modelMap, HttpServletResponse response, HttpSession session) {
        //If the owner left
        if(ludoMatchService.findludoMatchByMatchCode(matchCode).get().getClosedLobby() == 1){
            session.setAttribute("ownerLeft", "The owner of the lobby left, so it was closed");
            return "redirect:/";
        }

        //If the game started
        if(ludoMatchService.findludoMatchByMatchCode(matchCode).get().getStartDate() != null){
            return "redirect:/ludoMatches/"+ludoMatchService.findludoMatchByMatchCode(matchCode).get().getId();
        }

        response.addHeader("Refresh", REFRESH_RATE_LOBBY);

        LudoMatch ludoMatch = ludoMatchService.findludoMatchByMatchCode(matchCode).get();
        Boolean logged = userService.isAuthenticated();

        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            //Block to check if the player joined through malicious url
            Boolean isPlayerInLobby = false;
            for(PlayerLudoStats pls : ludoMatch.getStats()){
                if(pls.getPlayer().getUser().getUsername().equals(authenticatedUser.getUsername())){
                    isPlayerInLobby = true;
                }
            }

            if(isPlayerInLobby == false){
                return "redirect:/";
            }

            modelMap.addAttribute("numberOfPlayers", ludoMatch.getStats().size());
            if(playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(), ludoMatch.getId()).isPresent()) {
                modelMap.addAttribute("isOwner", playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(), ludoMatch.getId()).get().getIsOwner());
            }
            modelMap.addAttribute("stats", ludoMatch.getStats());
            modelMap.addAttribute("matchCode", matchCode);
            modelMap.addAttribute("match", ludoMatch);
            modelMap.addAttribute("matchId",ludoMatch.getId());

            return "matches/ludoMatchLobby";
        }else{
            return "redirect:/";
        }

    }

    @GetMapping(value = "/ludoMatches/{matchId}")
    public String showMatch(@PathVariable("matchId") Integer matchId, ModelMap model,
                            HttpServletRequest request, HttpSession session, HttpServletResponse response) throws InvalidPlayerNumberException {
        response.addHeader("Refresh", REFRESH_RATE_MATCH);
        Boolean logged = userService.isAuthenticated();
        List<LudoChip> chips = new ArrayList<LudoChip>();
        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player

            //To be able to redirect back when rolling the dice
            request.getSession().setAttribute("fromLudo", true);
            request.getSession().setAttribute("matchId", matchId);

            String view = "matches/ludoMatch";
            LudoMatch match = ludoMatchService.findludoMatchById(matchId).get();
            model.put("stats", match.getStats());

            //If the match is a new one, sets the start date and creates the board with its chips
            if (match.getStartDate() == null) {
                match.setStartDate(new Date());
                LudoBoard board = new LudoBoard();
                LudoBoard savedBoard = ludoBoardService.save(board, match.getStats());
                match.setBoard(savedBoard);
            }
            chips = (List<LudoChip>) ludoChipService.findChipsByMatchId(matchId);
            model.put("chips", chips);
            model.put("chipsToBeDisplaced", ludoChipService.checkOcuppied(chips));
            if (session.getAttribute("dices") != null) {
                int[] dices = (int[]) session.getAttribute("dices");
                model.put("firstDice", dices[0]);
                model.put("secondDice", dices[1]);
                model.put("sumDice", dices[2]);
            }

            PlayerLudoStats stats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(), matchId).get();

            //Checks if everyone except one left
            Boolean everyoneExceptOneLeft = ludoMatchService.findEveryoneExceptOneLeft(match);
            if (stats.getPlayerLeft() == 0 && everyoneExceptOneLeft == true) {
                stats.setHasWon(1);
                playerLudoStatsService.saveStats(stats);
            }
            ludoMatchService.save(match);

            //To show the other players if their game has been closed or has ended
            if(ludoMatchService.findludoMatchById(matchId).get().getEndDate() != null){
                //Calls achievementService to check new achievements
                for(PlayerLudoStats pls : match.getStats()){
                    Collection<PlayerLudoStats> statsInDb = playerLudoStatsService.findPlayerLudoStatsByUsername(pls.getPlayer().getUser().getUsername());
                    PlayerLudoStats sumStats = playerLudoStatsService.sumStats(statsInDb);
                    achievementService.checkLudoAchievements(sumStats);
                }
                model.addAttribute("hasEnded", 1);
                if(everyoneExceptOneLeft == true){
                    model.addAttribute("message", "Everyone except you left, so you won!");
                }else{
                    model.addAttribute("message", "The game has ended!");
                }
            } else {
                //To show if they rolled double dices or got blocked
                if (session.getAttribute("especial") != null) {
                    String mensaje = session.getAttribute("especial").toString();
                    model.put("message", mensaje);
                }
                if (!(stats.getHasTurn() < 0)) {
                    Integer hasTurn = stats.getHasTurn();
                    model.put("hasTurn", hasTurn);
                }
            }
            model.put("ludoBoard", ludoMatchService.findludoMatchById(match.getId()).get().getBoard());
            model.put("diceIndex", 0);
            return view;
        }else{
            return "redirect:/";
        }

    }

    @GetMapping(value="/ludoMatches")
    public String listadoPartidas(@RequestParam String page, ModelMap modelMap){
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.desc("startDate")));
        Slice<LudoMatch> slice = ludoMatchService.findAllPaging(pageable);
        String vista = "matches/listLudoMatches";
        modelMap.addAttribute("numberOfPages", Math.ceil(ludoMatchService.findAll().size()/NUMBER_OF_ELEMENTS_PER_PAGE));
        modelMap.addAttribute("ludoMatches",slice.getContent());
        return vista;
    }

    @PostMapping(value = "/ludoMatches")
    public String filterLudoMatches(ModelMap modelMap, @RequestParam String page, @RequestParam String filterBy, @RequestParam String date) {
        String vista = "matches/listLudoMatches";
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.desc("startDate")));
        String[] dateValues = date.split("-");
        if(dateValues.length == 3){
            Calendar correctDate = Calendar.getInstance();
            correctDate.set(Integer.parseInt(dateValues[0]),Integer.parseInt(dateValues[1])-1,Integer.parseInt(dateValues[2]));
            correctDate.set(Calendar.HOUR_OF_DAY,0);
            Date correctDateRepresentation = correctDate.getTime();
            if(filterBy.equals("startDate")){
                Slice<LudoMatch> matches = ludoMatchService.findMatchesByStartDate(correctDateRepresentation, pageable);
                modelMap.addAttribute("ludoMatches",matches.getContent());
                modelMap.addAttribute("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
            } else{
                Slice<LudoMatch> matches = ludoMatchService.findMatchesByEndDate(correctDateRepresentation, pageable);
                modelMap.addAttribute("ludoMatches",matches.getContent());
                modelMap.addAttribute("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
            }
        }else{
            Slice<LudoMatch> matches = ludoMatchService.findAllPaging(pageable);
            modelMap.addAttribute("ludoMatches",matches.getContent());
            modelMap.addAttribute("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
        }

        return vista;
    }

    @GetMapping(value="/ludoMatches/close/{matchId}")
    public String closeMatch(@PathVariable("matchId") Integer matchId){
        LudoMatch ludoMatchDb=ludoMatchService.findludoMatchById(matchId).get();
        ludoMatchDb.setEndDate(new Date());
        ludoMatchService.save(ludoMatchDb);
        return "redirect:/ludoMatches?page=0";

    }

    @GetMapping(value="/ludoMatches/stats/{matchId}")
    public ModelAndView showStats(@PathVariable("matchId") Integer matchId){
        LudoMatch ludoMatchDb=ludoMatchService.findludoMatchById(matchId).get();
        Collection<PlayerLudoStats> pls = ludoMatchDb.getStats();
        ModelAndView mav = new ModelAndView("stats/adminMatchStats");
        mav.addObject("ludoStats", pls);
        return mav;
    }

    @GetMapping(value = "/ludoMatches/matchLeft")
    public ModelAndView leaveMatch() {
        ModelAndView mav = new ModelAndView("welcome");
        Boolean logged = userService.isAuthenticated();

        if(logged == true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            LudoMatch userMatch = ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername()).get();
            Collection<PlayerLudoStats> plsColl = playerLudoStatsService.findPlayerLudoStatsByGame(userMatch.getId());
            PlayerLudoStats pls = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(authenticatedUser.getUsername(), userMatch.getId()).get();

            if(pls.getIsOwner() == 1 && userMatch.getStartDate() == null) {
                ludoMatchService.removeAllLudoStatsFromGame(userMatch.getId());
                userMatch.setClosedLobby(1);
                ludoMatchService.save(userMatch);
                mav.addObject("message", "You were the owner and left the game, so the lobby was closed!");
            } else if (userMatch.getStartDate() == null) {
                ludoMatchService.removeLudoStatsFromGame(pls, plsColl, userMatch);
                mav.addObject("message", "You left the lobby");
            } else {
                pls.setPlayerLeft(1);
                pls.setHasTurn(Integer.MIN_VALUE);
                playerLudoStatsService.saveStats(pls);
                mav.addObject("message", "You left the game!");
            }
        }
        return mav;
    }
}
