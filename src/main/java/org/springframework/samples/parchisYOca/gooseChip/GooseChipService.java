package org.springframework.samples.parchisYOca.gooseChip;

import org.hibernate.envers.internal.tools.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchRepository;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GooseChipService {

    public static final int CASILLA_FINAL = 63;
    public static final int CASILLA_INICIAL = 0;
    public static final int ULTIMA_OCA = 59;
    public static final int POSADA = 19;
    public static final int LABERINTO = 42;
    public static final int FINAL_LABERINTO = 30;
    public static final int CARCEL = 56;
    public static final int CALAVERA = 58;
    public static final List<Integer> OCAS = List.of(5,9,14,18,23,27,32,36,41,45,50,54);
    public static final List<Integer> DADOS = List.of(26,53);
    public static final List<Integer> PUENTES = List.of(6,12);
    public static final List<Integer> ESPECIALES = List.of(63,59,19,42,30,56,58,5,9,14,18,23,27,32,36,41,45,50,54,26,53,6,12);

    private GooseChipRepository gooseChipRepository;
    private PlayerGooseStatsRepository playerGooseStatsRepository;
    private GooseMatchRepository gooseMatchRepository;

    @Autowired
    public GooseChipService(GooseChipRepository gooseChipRepository, PlayerGooseStatsRepository playerGooseStatsRepository, GooseMatchRepository gooseMatchRepository){
        this.gooseChipRepository = gooseChipRepository;
        this.playerGooseStatsRepository = playerGooseStatsRepository;
        this.gooseMatchRepository = gooseMatchRepository;
    }

    @Transactional(readOnly = true)
    public Optional<GooseChip> findById(Integer id) throws DataAccessException {
        return gooseChipRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Collection<GooseChip> findChipsByMatchId(Integer matchId) throws DataAccessException {
        return gooseChipRepository.findChipsByMatchId(matchId);
    }

    @Transactional
    public GooseChip save(GooseChip gooseChip) throws DataAccessException {
        return gooseChipRepository.save(gooseChip);
    }

    @Transactional
    public Triple<Integer,Integer,String> checkSpecials(GooseChip chip, Integer suma, Boolean flagDobles) throws DataAccessException{
        Integer matchId = chip.getBoard().getMatch().getId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        PlayerGooseStats stats = playerGooseStatsRepository.findPlayerGooseStatsByUsernamedAndMatchId(currentUser.getUsername(), matchId).get();
        Integer nuevaPosicion = chip.getPosition()+suma;

        if(ESPECIALES.contains(nuevaPosicion)){
            if(OCAS.contains(nuevaPosicion)){
                return gooseHandler(nuevaPosicion,stats);
            } else if(PUENTES.contains(nuevaPosicion)){
                return bridgeHandler(nuevaPosicion,stats);
            } else if(DADOS.contains(nuevaPosicion)){
                return diceHandler(nuevaPosicion,stats);
            } else if(POSADA==nuevaPosicion){
                return innHandler(nuevaPosicion,stats);
            } else if(CARCEL==nuevaPosicion){
                return jailHandler(nuevaPosicion,stats);
            } else if(LABERINTO==nuevaPosicion){
                return mazeHandler(stats);
            } else if(CASILLA_FINAL == nuevaPosicion || ULTIMA_OCA == nuevaPosicion){
                return lastPositionHandler(matchId,stats);
            } else{
                return deathHandler(stats);
            }
        } else{ //Si no es especial se tiene en cuenta si es una tirada doble o no, tambien comprueba si ha habido rebote
            if(nuevaPosicion > CASILLA_FINAL){
                Integer diff = nuevaPosicion - CASILLA_FINAL;
                nuevaPosicion = CASILLA_FINAL - diff;
            }if(flagDobles==true){
                Triple<Integer,Integer, String> posicionYTurno=new Triple<>(nuevaPosicion,1,"Double roll");
                return posicionYTurno;
            } else {
                Triple<Integer, Integer, String> posicionYTurno = new Triple<>(nuevaPosicion, 0, "NoDobles");
                return posicionYTurno;
            }
        }
    }



    @Transactional
    public Triple<Integer,Integer,String> gooseHandler(Integer nuevaPosicion, PlayerGooseStats stats) throws DataAccessException{
        Integer oca=OCAS.indexOf(nuevaPosicion);
        Integer posicionDef;

        if(oca==OCAS.size()-1){
            posicionDef=ULTIMA_OCA;
        }else{
            posicionDef=(OCAS.get(oca+1));
        }

        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(posicionDef,1,"Goose");
        stats.setLandedGeese(stats.getLandedGeese()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }

    @Transactional
    public Triple<Integer,Integer,String> bridgeHandler(Integer nuevaPosicion, PlayerGooseStats stats) throws DataAccessException {
        Integer puente=PUENTES.indexOf(nuevaPosicion);
        Integer posicionDef=PUENTES.get((puente+1)%PUENTES.size());
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(posicionDef,1,"Bridge");
        stats.setLandedBridges(stats.getLandedBridges()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }

    @Transactional
    public Triple<Integer, Integer, String> diceHandler(Integer nuevaPosicion, PlayerGooseStats stats) throws DataAccessException {
        Integer dado = DADOS.indexOf(nuevaPosicion);
        Integer posicionDef=(DADOS.get((dado+1)%DADOS.size()));
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(posicionDef,1,"Dice");
        stats.setLandedDice(stats.getLandedDice()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }
    @Transactional
    public Triple<Integer, Integer, String> innHandler(Integer nuevaPosicion, PlayerGooseStats stats) throws DataAccessException{
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(nuevaPosicion,-1,"Inn");
        stats.setLandedInn(stats.getLandedInn()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }

    @Transactional
    public Triple<Integer, Integer, String> jailHandler(Integer nuevaPosicion, PlayerGooseStats stats) throws DataAccessException{
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(nuevaPosicion,-2,"Jail");
        stats.setLandedJails(stats.getLandedJails()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }

    @Transactional
    public Triple<Integer, Integer, String> mazeHandler(PlayerGooseStats stats) throws DataAccessException{
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(FINAL_LABERINTO,0,"Maze");
        stats.setLandedMaze(stats.getLandedMaze()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }

    @Transactional
    public Triple<Integer, Integer, String> deathHandler(PlayerGooseStats stats) throws DataAccessException{
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(CASILLA_INICIAL, 0,"Death");
        stats.setLandedDeath(stats.getLandedDeath()+1);
        playerGooseStatsRepository.save(stats);
        return posicionYTurno;
    }

    @Transactional
    public Triple<Integer, Integer, String> lastPositionHandler(Integer matchId, PlayerGooseStats stats) throws DataAccessException{
        Triple<Integer,Integer,String> posicionYTurno = new Triple<>(CASILLA_FINAL, 0, "Final");
        stats.setLandedGeese(stats.getLandedGeese()+1);
        stats.setHasWon(1);
        GooseMatch gooseMatch = gooseMatchRepository.findById(matchId).get();
        gooseMatch.setEndDate(new Date());
        playerGooseStatsRepository.save(stats);
        gooseMatchRepository.save(gooseMatch);
        return posicionYTurno;
    }

    /*
    @Transactional
    public GooseChip save(GooseChip gooseChip) throws DataAccessException{
        Integer matchId = gooseChip.getBoard().getMatch().getId();
        GooseMatch gooseMatch = gooseMatchRepository.findById(matchId).get();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        PlayerGooseStats stats = playerGooseStatsRepository.
            findPlayerGooseStatsByUsernamedAndMatchId(currentUser.getUsername(), matchId).get();

        Integer position = gooseChip.getPosition();

        //Checks if has won
        if(position == CASILLA_FINAL || position == ULTIMA_OCA){
            if(position == ULTIMA_OCA){
                gooseChip.setPosition(63);
            }
            gooseMatch.setEndDate(new Date());
            stats.setHasWon(1);
            gooseMatchRepository.save(gooseMatch);
        //Checks if bounce
        } else if (position > CASILLA_FINAL){
            Integer diff = position - CASILLA_FINAL;
            gooseChip.setPosition(CASILLA_FINAL- diff);
        //Checks if oca
        } else if (OCAS.contains(position)){
            Integer oca = OCAS.indexOf(position);
            if(oca == OCAS.size()-1){
                gooseChip.setPosition(ULTIMA_OCA);
            }
            gooseChip.setPosition(OCAS.get(oca+1));
            stats.setHasTurn(1);
        //Checks if puente
        } else if (PUENTES.contains(position)){
            Integer puente = PUENTES.indexOf(position);
            gooseChip.setPosition(PUENTES.get((puente+1)%PUENTES.size()));
            stats.setHasTurn(1);
        //Checks if dado
        } else if (DADOS.contains(position)){
            Integer dado = DADOS.indexOf(position);
            gooseChip.setPosition(DADOS.get((dado+1)%DADOS.size()));
            stats.setHasTurn(1);
        //Checks if posada
        } else if (position == POSADA){
            stats.setHasTurn(-1);
        //Checks if carcel
        } else if (position == CARCEL){
            stats.setHasTurn(-2);
        //Checks if laberinto
        } else if (position == LABERINTO){
            gooseChip.setPosition(FINAL_LABERINTO);
        //Checks if calavera
        }else if (position == CALAVERA){
            gooseChip.setPosition(0);
        }

        playerGooseStatsRepository.save(stats);
        gooseChipRepository.save(gooseChip);
        return gooseChip;
    }
    */


}
