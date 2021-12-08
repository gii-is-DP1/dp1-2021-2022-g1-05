package org.springframework.samples.parchisYOca.gooseChip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseBoard.GooseBoardRepository;
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
    public static final int ULTIMA_OCA = 59;
    public static final int POSADA = 19;
    public static final int LABERINTO = 42;
    public static final int FINAL_LABERINTO = 30;
    public static final int CARCEL = 56;
    public static final int CALAVERA = 58;
    public static final List<Integer> OCAS = List.of(5,9,14,18,23,27,32,36,41,45,50,54);
    public static final List<Integer> DADOS = List.of(26,53);
    public static final List<Integer> PUENTES = List.of(6,12);


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
}
