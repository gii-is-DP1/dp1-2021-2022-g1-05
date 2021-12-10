package org.springframework.samples.parchisYOca.gooseChip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatchRepository;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStatsRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GooseChipAuxService {
    //comprobar porque he tenido que añadir el goosechipservice para pillar la lsita de especiales

    private GooseChipRepository gooseChipRepository;
    private PlayerGooseStatsRepository playerGooseStatsRepository;
    private GooseMatchRepository gooseMatchRepository;
    private GooseChipService gooseChipService;

    @Autowired
    public GooseChipAuxService(GooseChipRepository gooseChipRepository, PlayerGooseStatsRepository playerGooseStatsRepository, GooseMatchRepository gooseMatchRepository, GooseChipService gooseChipService){
        this.gooseChipRepository = gooseChipRepository;
        this.playerGooseStatsRepository = playerGooseStatsRepository;
        this.gooseMatchRepository = gooseMatchRepository;
        this.gooseChipService=gooseChipService;
    }

    //TODO no se que debería devolver exactamente de momento sera un array de dos cosas la posicion nueva y si tiene o no turno
    @Transactional
    public Integer[]  checkSpecials(GooseChip chip,Integer suma,Boolean flag){
        Integer matchId = chip.getBoard().getMatch().getId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        PlayerGooseStats stats = playerGooseStatsRepository.findPlayerGooseStatsByUsernamedAndMatchId(currentUser.getUsername(), matchId).get();
        Integer[] res=new Integer[2];
        Integer nuevaPosicion=chip.getPosition()+suma;
        //Aquí no comprueba el rebote ni si ha ganado
        if(gooseChipService.getEspeciales().contains(nuevaPosicion)){
            if(gooseChipService.OCAS.contains(nuevaPosicion)){
                return gooseHandler(chip,suma,flag,nuevaPosicion,stats);
            }
            else if(gooseChipService.PUENTES.contains(nuevaPosicion)){
                return bridgeHandler(chip,suma, flag, nuevaPosicion,stats);
            }
            else if(gooseChipService.DADOS.contains(nuevaPosicion)){
                return diceHandler(chip,suma,flag,nuevaPosicion,stats);
            }
            else if(gooseChipService.POSADA==nuevaPosicion){
                return innHandler(chip,suma,flag,nuevaPosicion,stats);
            }
            else if(gooseChipService.CARCEL==nuevaPosicion){
                return jailHandler(chip,suma,flag,nuevaPosicion,stats);
            }
            else if(gooseChipService.LABERINTO==nuevaPosicion){
                return mazeHandler(chip,suma,flag,nuevaPosicion,stats);
            }
            //calavera
            else{
                return deathHandler(stats);
            }
        }
        //Si no es especial se tiene en cuenta si es una tirada doble o no
        else{
            if(flag==true){
                res[0]=chip.getPosition()+suma;
                res[1]=1;
            }
            else{
                res[0]=chip.getPosition()+suma;
                res[1]=0;
            }
            return res;
        }
    }

    private Integer[] gooseHandler(GooseChip chip, Integer suma, Boolean flag, Integer nuevaPosicion, PlayerGooseStats stats) {
        Integer oca=gooseChipService.OCAS.indexOf(nuevaPosicion);
        Integer posicionDef;
        Integer[] res= new Integer[2];
        if(oca==gooseChipService.OCAS.size()-1){
            posicionDef=gooseChipService.ULTIMA_OCA;
        }else{
            posicionDef=(gooseChipService.OCAS.get(oca)+1);
        }//Nueva posición y que tiene turno
        res[0]=posicionDef;
        res[1]=1;
        stats.setLandedGeese(stats.getLandedGeese()+1);
        return res;
    }
    private Integer[] bridgeHandler(GooseChip chip, Integer suma, Boolean flag, Integer nuevaPosicion, PlayerGooseStats stats) {
        Integer posicionDef;
        Integer[] res=new Integer[2];
        Integer puente=gooseChipService.PUENTES.indexOf(nuevaPosicion);
        posicionDef=gooseChipService.PUENTES.get((puente+1)%gooseChipService.PUENTES.size());
        res[0]=posicionDef;
        res[1]=1;
        stats.setLandedBridges(stats.getLandedBridges()+1);
        return res;
    }
}
