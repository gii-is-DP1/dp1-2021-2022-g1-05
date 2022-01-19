package org.springframework.samples.parchisYOca.gooseMatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.samples.parchisYOca.achievement.AchievementService;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardService;
import org.springframework.samples.parchisYOca.gooseBoard.exceptions.InvalidPlayerNumberException;
import org.springframework.samples.parchisYOca.gooseChip.GooseChipService;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
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
@Slf4j
public class GooseMatchController {

    private final GooseMatchService gooseMatchService;
    private final PlayerService playerService;
    private final PlayerGooseStatsService playerGooseStatsService;
    private final GooseBoardService gooseBoardService;
    private final GooseChipService gooseChipService;
    private final UserService userService;
    private final AchievementService achievementService;

    private static final Integer MATCH_CODE_LENGTH = 6;
    private static final Integer MAX_NUMBER_OF_PLAYERS = 4;
    private static final String REFRESH_RATE_MATCH = "2";
    private static final String REFRESH_RATE_LOBBY = "3";
    private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 6;

    @Autowired
    public GooseMatchController(GooseMatchService gooseMatchService, PlayerService playerService,
                                PlayerGooseStatsService playerGooseStatsService,
                                GooseBoardService gooseBoardService, GooseChipService gooseChipService,
                                UserService userService, AchievementService achievementService){
        this.gooseMatchService = gooseMatchService;
        this.playerService = playerService;
        this.playerGooseStatsService = playerGooseStatsService;
        this.gooseBoardService = gooseBoardService;
        this.gooseChipService = gooseChipService;
        this.userService = userService;
        this.achievementService = achievementService;
    }

    @GetMapping("/gooseMatches/new")
    public String createGooseMatch(ModelMap modelMap){
    	log.info("Creating a new Goose match");
        String matchCode = RandomStringGenerator.getRandomString(MATCH_CODE_LENGTH);
        GooseMatch gooseMatch = new GooseMatch();
        Boolean logged = userService.isAuthenticated();
        log.debug("Checking if user is authenticated");
        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            log.debug("Player '{}' has been authenticated", player.getUser().getUsername());
            log.debug("Getting {}'s Goose stats", player.getUser().getUsername());
            Optional<GooseMatch> playerInGooseMatches = gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername());
            if(playerInGooseMatches.isPresent()){
            	log.debug("{}'s Goose stats have been found", player.getUser().getUsername());
                GooseMatch playerInGooseMatch = playerInGooseMatches.get();
                modelMap.addAttribute("message", "You are already at a lobby: "+playerInGooseMatch.getMatchCode());
                return "redirect:/";
            }
            log.debug("Generating new Goose match with code '{}'", matchCode);
            gooseMatch.setMatchCode(matchCode);
            gooseMatchService.saveGooseMatchWithPlayer(gooseMatch, player, true);
            return "redirect:/gooseMatches/lobby/"+matchCode;
        }else{
        	log.debug("Player wasn't authenticated");
            return "redirect:/";
        }

    }

    @GetMapping(value = "/gooseMatches/join")
    public String  joinGooseMatchForm(Map<String, Object> model){
    	log.debug("Joining a new match");
        GooseMatch match = new GooseMatch();
        model.put("match", match);
        return "matches/joinMatchForm";
    }

    @PostMapping(value = "/gooseMatches/join")
    public String joinGooseMatch(@RequestParam String matchCode, ModelMap modelMap){
    	log.debug("Joining Goose match with code {}",matchCode);
        Optional<GooseMatch> gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode);
        Boolean logged = userService.isAuthenticated();
        log.debug("Checking if player is authenticated");
        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            log.debug("Player '{}' is trying to join Goose match with code '{}'", player.getUser().getUsername(), matchCode);
            Optional<GooseMatch> playerInGooseMatches = gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername());
            if(playerInGooseMatches.isPresent()){ //If player is in a match
                if(matchCode.equals(playerInGooseMatches.get().getMatchCode())){
                	log.debug("Redirecting to Goose lobby with code '{}'", matchCode);//If the player enters the lobby they are in
                    return "redirect:/gooseMatches/lobby/" + matchCode;
                }else{
                	log.debug("The player '{}' was already in the Goose lobby with code '{}'",player.getUser().getUsername(),matchCode);
                    modelMap.addAttribute("message", "You are already at a lobby: "+playerInGooseMatches.get().getMatchCode());
                }
            } else{
                if(gooseMatch.isPresent()) { //If the game exists
                    if(!(gooseMatch.get().getStats().size()>=MAX_NUMBER_OF_PLAYERS)) { //If the game is not full
                    	log.debug("Saving player '{}' in Goose match with code '{}'", player.getUser().getUsername(), matchCode);
                        gooseMatchService.saveGooseMatchWithPlayer(gooseMatch.get(),player, false);
                        return "redirect:/gooseMatches/lobby/"+matchCode;
                    }else{
                    	log.debug("The lobby was full so '{}' couldn't enter", player.getUser().getUsername());
                        modelMap.addAttribute("message", "The lobby is full!");
                    }
                }else{
                	log.debug("Goose lobby with code '{}' was not found", matchCode);
                    modelMap.addAttribute("message", "Lobby not found!");
                }
            }
            return "matches/joinMatchForm";
        }else{
        	log.debug("Player wasn't authenticated");
            return "redirect:/";
        }

    }


    @GetMapping(value = "/gooseMatches/lobby/{matchCode}")
    public String initCreationLobby(@PathVariable("matchCode") String matchCode, ModelMap modelMap, HttpServletResponse response, HttpSession session) {
        //If the owner left
    	log.debug("Checking if the owner of the Goose match with code '{}' has left", matchCode);
        if(gooseMatchService.findGooseMatchByMatchCode(matchCode).get().getClosedLobby() == 1){
            session.setAttribute("ownerLeft", "The owner of the lobby left, so it was closed");
            log.debug("The owner of the Goose match with code '{}' was no where to be found", matchCode);
            return "redirect:/";
        }

        //If the game started
        log.debug("Checking if the Goose match with code '{}' has already begun", matchCode);
        if(gooseMatchService.findGooseMatchByMatchCode(matchCode).get().getStartDate() != null){
        	log.debug("The Goose match with code '{}' has already begun so we are redirecting", matchCode);
            return "redirect:/gooseMatches/"+gooseMatchService.findGooseMatchByMatchCode(matchCode).get().getId();
        }

        response.addHeader("Refresh", REFRESH_RATE_LOBBY);

        GooseMatch gooseMatch = gooseMatchService.findGooseMatchByMatchCode(matchCode).get();
        Boolean logged = userService.isAuthenticated();
        log.debug("Checking if player is authenticated");
        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            log.debug("Player '{}' has been identified", authenticatedUser.getUsername());
            //Block to check if the player joined through malicious url
            Boolean isPlayerInLobby = false;
            log.debug("Checking if player '{}' is already in a lobby", authenticatedUser.getUsername());
            for(PlayerGooseStats pgs : gooseMatch.getStats()){
                if(pgs.getPlayer().getUser().getUsername().equals(authenticatedUser.getUsername())){
                    isPlayerInLobby = true;
                }
            }

            if(isPlayerInLobby == false){
                return "redirect:/";
            }

            modelMap.addAttribute("numberOfPlayers", gooseMatch.getStats().size());
            if(playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(), gooseMatch.getId()).isPresent()){
                modelMap.addAttribute("isOwner", playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(), gooseMatch.getId()).get().getIsOwner());
            }
            modelMap.addAttribute("stats", gooseMatch.getStats());
            modelMap.addAttribute("matchCode", matchCode);
            modelMap.addAttribute("match", gooseMatch);
            modelMap.addAttribute("matchId",gooseMatch.getId());

            return "matches/gooseMatchLobby";
        }else{
            return "redirect:/";
        }
    }


    @GetMapping(value = "/gooseMatches/{matchId}")
    public String showMatch(@PathVariable("matchId") Integer matchId, ModelMap model,
                            HttpServletRequest request, HttpSession session, HttpServletResponse response) throws InvalidPlayerNumberException {

        response.addHeader("Refresh", REFRESH_RATE_MATCH);
        Boolean logged = userService.isAuthenticated();
        log.debug("Checking if user is authenticated");
        if(logged==true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            log.debug("Player '{}' has been authenticated", authenticatedUser.getUsername());
            //To be able to redirect back when rolling the dice
            log.debug("Setting the origin of the request and the match id");
            request.getSession().setAttribute("fromGoose", true);
            request.getSession().setAttribute("matchId", matchId);

            String view = "matches/gooseMatch";
            log.debug("Finding Goose match and stats");
            GooseMatch match = gooseMatchService.findGooseMatchById(matchId).get();
            model.put("stats", match.getStats());

            //If the match is a new one, sets the start date and creates the board with its chips
            log.debug("Checking if the match has just been created and sets the date");
            if (match.getStartDate() == null) {
            	log.debug("Setting match start date");
                match.setStartDate(new Date());
                GooseBoard board = new GooseBoard();
                GooseBoard savedBoard = gooseBoardService.save(board, match.getStats().size());
                match.setBoard(savedBoard);
            }
            model.put("chips", gooseChipService.findChipsByMatchId(matchId));
            if (session.getAttribute("dices") != null) {
                int[] dices = (int[]) session.getAttribute("dices");
                model.put("firstDice", dices[0]);
                model.put("secondDice", dices[1]);
                model.put("sumDice", dices[2]);
            }

            PlayerGooseStats stats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(), matchId).get();

            //Checks if everyone except one left
            Boolean everyoneExceptOneLeft = gooseMatchService.findEveryoneExceptOneLeft(match);
            log.debug("Checking if everyone has left");
            if (stats.getPlayerLeft() == 0 && everyoneExceptOneLeft == true) {
            	log.debug("Everyone except '{}' has left", stats.getPlayer().getUser().getUsername());
                stats.setHasWon(1);
                playerGooseStatsService.saveStats(stats);
            }
            gooseMatchService.save(match);

            //To show the other players if their game has been closed or has ended
            log.debug("Checking if Goose Match with id '{}' has ended", matchId);
            if(gooseMatchService.findGooseMatchById(matchId).get().getEndDate() != null){
            	log.debug("The match with id '{}' has ended", matchId);
                //Calls achievementService to check new achievements
                for(PlayerGooseStats pgs : match.getStats()){
                    Collection<PlayerGooseStats> statsInDb = playerGooseStatsService.findPlayerGooseStatsByUsername(pgs.getPlayer().getUser().getUsername());
                    PlayerGooseStats sumStats = playerGooseStatsService.sumStats(statsInDb);
                    achievementService.checkGooseAchievements(sumStats);
                }
                model.addAttribute("hasEnded", 1);
                if(everyoneExceptOneLeft == true){
                	log.debug("Everyone except '{}' has left", stats.getPlayer().getUser().getUsername());
                    model.addAttribute("message", "Everyone except you left, so you won!");
                }else{
                	log.debug("The match with id '{}' has ended", matchId);
                    model.addAttribute("message", "The game has ended!");
                }
            }else{
            	log.debug("Checking if they have landed on a special square");
                //To show if they landed on a special square
                if (session.getAttribute("especial") != null){
                	log.debug("The player has landed on a special square");
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
        }else{
            return "redirect:/";
        }

    }

    @GetMapping(value="/gooseMatches")
    public String listadoPartidas(@RequestParam String page, ModelMap modelMap){
    	log.debug("Generating a new page to show Goose matches");
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.desc("startDate")));
        log.debug("The list of Goose matches to show has {} items and is page number {}", pageable.getPageSize(), pageable.getPageNumber());
        Slice<GooseMatch> slice = gooseMatchService.findAllPaging(pageable);
        String vista = "matches/listGooseMatches";
        modelMap.addAttribute("numberOfPages", Math.ceil(gooseMatchService.findAll().size()/NUMBER_OF_ELEMENTS_PER_PAGE));
        modelMap.addAttribute("gooseMatches",slice.getContent());
        log.debug("Showing list of Goose matches");
        return vista;
    }

    @PostMapping(value = "/gooseMatches")
    public String filterGooseMatches(ModelMap modelMap, @RequestParam String page,  @RequestParam String filterBy, @RequestParam String date) {
        String vista = "matches/listGooseMatches";
        log.debug("Filtering Goose matches by '{}' and '{}'",filterBy, date);
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.desc("startDate")));
        log.debug("The list of filtered Goose matches to show has {} items and is page number {}", pageable.getPageSize(), pageable.getPageNumber());
        String[] dateValues = date.split("-");
        if(dateValues.length == 3){
            Calendar correctDate = Calendar.getInstance();
            correctDate.set(Integer.parseInt(dateValues[0]),Integer.parseInt(dateValues[1])-1,Integer.parseInt(dateValues[2]));
            correctDate.set(Calendar.HOUR_OF_DAY,0);
            Date correctDateRepresentation = correctDate.getTime();
            if(filterBy.equals("startDate")){
                Slice<GooseMatch> matches = gooseMatchService.findMatchesByStartDate(correctDateRepresentation, pageable);
                modelMap.addAttribute("gooseMatches",matches.getContent());
                modelMap.addAttribute("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
            } else{
                Slice<GooseMatch> matches = gooseMatchService.findMatchesByEndDate(correctDateRepresentation, pageable);
                modelMap.addAttribute("gooseMatches",matches.getContent());
                modelMap.addAttribute("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
            }
        }else{
            Slice<GooseMatch> matches = gooseMatchService.findAllPaging(pageable);
            modelMap.addAttribute("gooseMatches",matches.getContent());
            modelMap.addAttribute("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
        }
        return vista;
    }

    @GetMapping(value="/gooseMatches/close/{matchId}")
    public String closeMatch(@PathVariable("matchId") Integer matchId){
    	log.debug("Closing Goose match with id '{}'", matchId);
        GooseMatch gooseMatchDb=gooseMatchService.findGooseMatchById(matchId).get();
        gooseMatchDb.setEndDate(new Date());
        gooseMatchService.save(gooseMatchDb);
        return "redirect:/gooseMatches?page=0";

    }

    @GetMapping(value="/gooseMatches/stats/{matchId}")
    public ModelAndView showStats(@PathVariable("matchId") Integer matchId){
    	log.debug("Showing stats from Goose match with id '{}'", matchId);
        GooseMatch gooseMatchDb=gooseMatchService.findGooseMatchById(matchId).get();
        Collection<PlayerGooseStats> pgs = gooseMatchDb.getStats();
        ModelAndView mav = new ModelAndView("stats/adminMatchStats");
        mav.addObject("gooseStats", pgs);
        return mav;

    }

    @GetMapping(value="/gooseMatches/matchLeft")
    public ModelAndView leaveMatch() {
        ModelAndView mav = new ModelAndView("welcome");
        Boolean logged = userService.isAuthenticated();
        log.debug("Showing welcome because the player has left the Goose match");
        if (logged == true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            GooseMatch userMatch = gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()).get();
            PlayerGooseStats pgs = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(authenticatedUser.getUsername(), userMatch.getId()).get();

            if (pgs.getIsOwner() == 1 && userMatch.getStartDate() == null) {
                gooseMatchService.removeAllGooseStatsFromGame(userMatch.getId());
                userMatch.setClosedLobby(1);
                gooseMatchService.save(userMatch);
                mav.addObject("message", "You were the owner and left the game, so the lobby was closed!");
            } else if (userMatch.getStartDate() == null) {
                gooseMatchService.removeGooseStatsFromGame(pgs, userMatch.getId());
                mav.addObject("message", "You left the lobby");
            } else {
                pgs.setPlayerLeft(1);
                pgs.setHasTurn(Integer.MIN_VALUE);
                playerGooseStatsService.saveStats(pgs);
                mav.addObject("message", "You left the game!");
            }
        }
        return mav;
    }
}
