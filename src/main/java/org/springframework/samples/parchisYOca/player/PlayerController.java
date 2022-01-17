package org.springframework.samples.parchisYOca.player;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchService;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatchService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsService;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStatsService;
import org.springframework.samples.parchisYOca.user.UserService;
import org.springframework.samples.parchisYOca.util.Durations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.*;


@Controller
@Slf4j
public class PlayerController {

    private static final String VIEWS_PLAYER_UPDATE_FORM = "players/UpdatePlayerForm";
    private static final Integer NUMBER_OF_ELEMENTS_PER_PAGE = 6;


    private final PlayerService playerService;
    private final UserService userService;
    private final GooseMatchService gooseMatchService;
    private final LudoMatchService ludoMatchService;
    private final PlayerGooseStatsService playerGooseStatsService;
    private final PlayerLudoStatsService playerLudoStatsService;

    @Autowired
    public PlayerController(PlayerService playerService, UserService userService,
                            GooseMatchService gooseMatchService, LudoMatchService ludoMatchService,
                            PlayerGooseStatsService playerGooseStatsService, PlayerLudoStatsService playerLudoStatsService) {
        this.playerService = playerService;
        this.userService = userService;
        this.gooseMatchService = gooseMatchService;
        this.ludoMatchService = ludoMatchService;
        this.playerLudoStatsService = playerLudoStatsService;
        this.playerGooseStatsService = playerGooseStatsService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @GetMapping("/players/ownProfile")
    public String redirectToProfile(){
        Boolean logged = userService.isAuthenticated();
        if(logged==true) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();
            log.info("Showing {}'s profile", currentUser.getUsername());
            Player player = playerService.findPlayerByUsername(currentUser.getUsername()).get();
            Integer playerId = player.getId();
            return "redirect:/players/"+playerId;
        }
        log.info("Someno who was not logged in tried to see their own profile");
        return "redirect:/";

    }


    @GetMapping("/players/{playerId}")
    public ModelAndView showPlayer(@PathVariable("playerId") int playerId) {
        ModelAndView mav = new ModelAndView("players/playerDetails");
        Boolean logged = userService.isAuthenticated();
        log.info("Showing page of player with id: {}", playerId);
        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player player = playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            List<GrantedAuthority> authorities = new ArrayList<>(authenticatedUser.getAuthorities()); //Gets lists of authorities
            log.info("Checking {}'s authorities", player.getUser().getUsername());
            if (gooseMatchService.findLobbyByUsername(authenticatedUser.getUsername()).isPresent() ||
                ludoMatchService.findLobbyByUsername(authenticatedUser.getUsername()).isPresent()){
            	log.debug("{} is playing a game", player.getUser().getUsername());
                mav.addObject("inGame","true"); //Not delete account when in game
            }
            if (playerId == player.getId() || authorities.get(0).toString().equals("admin")){
            	log.debug("{} is owner of the profile or admin", player.getUser().getUsername());
                mav.addObject("hasPermission", "true"); //To check if user has permission to see data
            }

            Collection<PlayerGooseStats> gooseStatsOfPlayer = playerGooseStatsService.findPlayerGooseStatsByUsername(authenticatedUser.getUsername());
            Collection<PlayerLudoStats> ludoStatsOfPlayer = playerLudoStatsService.findPlayerLudoStatsByUsername(authenticatedUser.getUsername());

            //To show user stats
            PlayerGooseStats playerGooseStats = playerGooseStatsService.sumStats(gooseStatsOfPlayer);
            PlayerLudoStats playerLudoStats = playerLudoStatsService.sumStats(ludoStatsOfPlayer);
            List<Long> durationsGoose = new ArrayList<>();
            List<Long> durationsLudo = new ArrayList<>();

            for(PlayerGooseStats pgs : gooseStatsOfPlayer){
                GooseMatch gm = pgs.getGooseMatch();
                if(gm.getEndDate() != null){
                    Long millis = gm.getEndDate().getTime() - gm.getStartDate().getTime();
                    durationsGoose.add(millis);
                }
            }

            for(PlayerLudoStats pls : ludoStatsOfPlayer){
                LudoMatch lm = pls.getLudoMatch();
                if(lm.getEndDate() != null){
                    Long millis = lm.getEndDate().getTime() - lm.getStartDate().getTime();
                    durationsGoose.add(millis);
                }
            }

            String averageGoose = Durations.listDurationsToString(durationsGoose);
            String averageLudo = Durations.listDurationsToString(durationsLudo);

            mav.addObject("numberOfGooseGames", gooseStatsOfPlayer.size());
            mav.addObject("numberOfLudoGames", ludoStatsOfPlayer.size());
            mav.addObject("gooseStats", playerGooseStats);
            mav.addObject("ludoStats", playerLudoStats);
            mav.addObject("averageGooseDuration", averageGoose);
            mav.addObject("averageLudoDuration", averageLudo);


        }



        mav.addObject(this.playerService.findPlayerById(playerId).get());
        mav.addObject(this.playerService.findPlayerById(playerId).get().getUser());
        return mav;
    }

    @GetMapping(value = "/players")
    public String showAllPlayers(@RequestParam String page, ModelMap modelMap) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.asc("user.username")));
        Slice<Player> slice = playerService.findAllPaging(pageable);
        String playersInGame = "";

        for(Player p : slice.getContent()){
            if (gooseMatchService.findLobbyByUsername(p.getUser().getUsername()).isPresent() ||
            ludoMatchService.findLobbyByUsername(p.getUser().getUsername()).isPresent()){
                playersInGame = playersInGame + p.getUser().getUsername() + " ";
            }
        }

        modelMap.addAttribute("numberOfPages", Math.ceil(playerService.findAll().size()/NUMBER_OF_ELEMENTS_PER_PAGE));
        modelMap.addAttribute("players", slice.getContent());
        modelMap.addAttribute("playersInGame", playersInGame);
        return "players/listPlayers";
    }

    @PostMapping(value = "/players")
    public String filterPlayers(ModelMap modelMap, @RequestParam String page, @RequestParam String Username) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.asc("user.username")));
        String vista = "players/listPlayers";
        Slice<Player> players = playerService.findAllFilteringByUsername(Username, pageable);
        modelMap.addAttribute("players", players.getContent());
        modelMap.addAttribute("numberOfPages", Math.ceil(players.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
        return vista;
    }


    @GetMapping(value = "/players/{playerId}/edit")
    public String initUpdatePlayerForm(@PathVariable("playerId") int playerId, Model model) {

        Boolean logged = userService.isAuthenticated();

        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player authenticatedPlayer = this.playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();
            List<GrantedAuthority> authorities = new ArrayList<>(authenticatedUser.getAuthorities()); //Gets lists of authorities

            if (playerId == authenticatedPlayer.getId() || authorities.get(0).toString().equals("admin")){
                model.addAttribute("hasPermission", "true");  //To check if user has permission to see data
                Player player = playerService.findPlayerById(playerId).get();
                model.addAttribute(player);
                return VIEWS_PLAYER_UPDATE_FORM;
            }

        }
        return "redirect:";
    }

    @PostMapping(value = "/players/{playerId}/edit")
    public String processUpdatePlayerForm(@Valid Player player, BindingResult result,
                                         @PathVariable("playerId") int playerId) {
        if (result.hasErrors()) {
            return VIEWS_PLAYER_UPDATE_FORM;
        }
        else {
            player.setId(playerId);
            this.playerService.savePlayer(player);
            return "redirect:/players/{playerId}";
        }
}

    @GetMapping(path="/players/disable/{playerId}")
    public String disablePlayer(@PathVariable("playerId") int playerId){
        Player player = playerService.findPlayerById(playerId).get();
        if(!player.equals(null)){
            playerService.disable(player);
        }
        return "redirect:/players?page=0";
    }

    @GetMapping(path="/players/enable/{playerId}")
    public String enablePlayer(@PathVariable("playerId") int playerId){
        Player player = playerService.findPlayerById(playerId).get();
        if(!player.equals(null)) {
            playerService.enable(player);
        }
        return "redirect:/players?page=0";
    }

    @GetMapping(path="/players/{playerId}/delete")
    public String deletePlayer(@PathVariable("playerId") int playerId, ModelMap modelMap){
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        Boolean logged = userService.isAuthenticated();

        if(logged == true){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal(); //Gets user and logged in player
            Player authenticatedPlayer = this.playerService.findPlayerByUsername(authenticatedUser.getUsername()).get();

            if(playerId == authenticatedPlayer.getId()){ //If auto-deletes, logs off
                authentication.setAuthenticated(false);
            }

            if(playerOptional.isPresent()){
                Player player = playerOptional.get();
                playerService.delete(player);
                modelMap.addAttribute("message", "Player successfully deleted!");

            }else{
                modelMap.addAttribute("message", "Player not found!");
            }
        }

        return "welcome";
    }

    @GetMapping(path="/players/{playerId}/ludoMatchesPlayed")
    public ModelAndView ludoMatchesOfPlayer(@RequestParam String page, @PathVariable("playerId") int playerId){
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.desc("startDate")));
        ModelAndView mav = new ModelAndView("matches/listMatchesInProfile");
        Slice<LudoMatch> matches = ludoMatchService.findMatchesByUsernameWithPaging(playerOptional.get().getUser().getUsername(), pageable);
        Map<LudoMatch,String> matchesAndWinners = new HashMap<>();
        for(LudoMatch ludoMatch : matches.getContent()){
            if(playerService.findWinnerByLudoMatchCode(ludoMatch.getMatchCode()).isPresent()){
                String winner = playerService.findWinnerByLudoMatchCode(ludoMatch.getMatchCode()).get().getUser().getUsername();
                matchesAndWinners.put(ludoMatch, winner);
            }else{
                String winner = "No winner";
                matchesAndWinners.put(ludoMatch, winner);
            }
        }
        mav.addObject("gameFrom", "ludo");
        mav.addObject("playerId", playerId);
        mav.addObject("matches", matchesAndWinners);
        mav.addObject("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
        return mav;
    }

    @GetMapping(path="/players/{playerId}/gooseMatchesPlayed")
    public ModelAndView gooseMatchesOfPlayer(@RequestParam String page, @PathVariable("playerId") int playerId){
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        Pageable pageable = PageRequest.of(Integer.parseInt(page),NUMBER_OF_ELEMENTS_PER_PAGE, Sort.by(Sort.Order.desc("startDate")));
        ModelAndView mav = new ModelAndView("matches/listMatchesInProfile");
        Slice<GooseMatch> matches = gooseMatchService.findMatchesByUsernameWithPaging(playerOptional.get().getUser().getUsername(), pageable);
        Map<GooseMatch,String> matchesAndWinners = new HashMap<>();
        for(GooseMatch gooseMatch : matches.getContent()){
            if(playerService.findWinnerByGooseMatchCode(gooseMatch.getMatchCode()).isPresent()){
                String winner = playerService.findWinnerByGooseMatchCode(gooseMatch.getMatchCode()).get().getUser().getUsername();
                matchesAndWinners.put(gooseMatch, winner);
            }else{
                String winner = "No winner";
                matchesAndWinners.put(gooseMatch, winner);
            }
        }
        mav.addObject("gameFrom", "goose");
        mav.addObject("playerId", playerId);
        mav.addObject("matches", matchesAndWinners);
        mav.addObject("numberOfPages", Math.ceil(matches.getNumberOfElements()/NUMBER_OF_ELEMENTS_PER_PAGE));
        return mav;
    }

    @GetMapping(path="/players/{playerId}/matchStats/{matchCode}")
    public ModelAndView statsOfPlayerInMatch(@PathVariable("playerId") int playerId,
                                             @PathVariable("matchCode") String matchCode){
        ModelAndView mav = new ModelAndView("stats/userStatsInAMatch");
        Optional<Player> playerOptional = playerService.findPlayerById(playerId);
        String username = playerOptional.get().getUser().getUsername();

        if(gooseMatchService.findGooseMatchByMatchCode(matchCode).isPresent()){
            GooseMatch match = gooseMatchService.findGooseMatchByMatchCode(matchCode).get();
            PlayerGooseStats stats = playerGooseStatsService.findGooseStatsByUsernamedAndMatchId(username,match.getId()).get();
            mav.addObject("gooseStats", stats);
        }else{
            LudoMatch match = ludoMatchService.findludoMatchByMatchCode(matchCode).get();
            PlayerLudoStats stats = playerLudoStatsService.findPlayerLudoStatsByUsernameAndMatchId(username,match.getId()).get();
            mav.addObject("ludoStats", stats);
        }
        return mav;
    }

    @GetMapping(path="/stats")
    public ModelAndView totalStats(){
        ModelAndView mav = new ModelAndView("stats/totalStats");
        Iterable<PlayerGooseStats> iterableGooseStats = playerGooseStatsService.findAll();
        Iterable<PlayerLudoStats> iterableLudoStats = playerLudoStatsService.findAll();
        Set<PlayerGooseStats> setGooseStats = new HashSet<>();
        iterableGooseStats.forEach(setGooseStats::add);
        Set<PlayerLudoStats> setLudoStats = new HashSet<>();
        iterableLudoStats.forEach(setLudoStats::add);
        Iterable<GooseMatch> gooseMatches = gooseMatchService.findEndedGooseMatches();
        Iterable<LudoMatch> ludoMatches = ludoMatchService.findEndedLudoMatches();
        Integer numberOfGooseMatches = 0;
        Integer numberOfLudoMatches = 0;
        List<Long> durationsGoose = new ArrayList<>();
        List<Long> durationsLudo = new ArrayList<>();


        for(GooseMatch gm : gooseMatches){
            numberOfGooseMatches++;
            Long millis = gm.getEndDate().getTime() - gm.getStartDate().getTime();
            durationsGoose.add(millis);
        }

        for(LudoMatch lm : ludoMatches){
            numberOfLudoMatches++;
            Long millis = lm.getEndDate().getTime() - lm.getStartDate().getTime();
            durationsGoose.add(millis);
        }

        String mediaGoose = Durations.listDurationsToString(durationsGoose);
        String mediaLudo = Durations.listDurationsToString(durationsLudo);

        PlayerGooseStats totalGooseStats = playerGooseStatsService.sumStats(setGooseStats);
        PlayerLudoStats totalLudoStats = playerLudoStatsService.sumStats(setLudoStats);
        List<PlayerGooseStats> top3MostGooseWins = playerGooseStatsService.top3MostGooseWins(setGooseStats, "mostWins");
        List<PlayerGooseStats> top3MostGooseSquares = playerGooseStatsService.top3MostGooseWins(setGooseStats, "mostSquares");
        List<PlayerLudoStats> top3MostLudoWins = playerLudoStatsService.top3MostLudoWins(setLudoStats, "mostWins");
        List<PlayerLudoStats> top3MostLudoEatenTokens = playerLudoStatsService.top3MostLudoWins(setLudoStats, "eatenTokens");


        mav.addObject("numberOfLudoGames", numberOfGooseMatches);
        mav.addObject("numberOfGooseGames", numberOfLudoMatches);
        mav.addObject("averageGooseDuration", mediaGoose);
        mav.addObject("averageLudoDuration", mediaLudo);
        mav.addObject("top3GooseSquares", top3MostGooseSquares);
        mav.addObject("top3GooseWins", top3MostGooseWins);
        mav.addObject("top3EatenTokens", top3MostLudoEatenTokens);
        mav.addObject("top3LudoWins", top3MostLudoWins);
        mav.addObject("gooseStats", totalGooseStats);
        mav.addObject("ludoStats", totalLudoStats);
        return mav;
    }



}
