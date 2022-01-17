package org.springframework.samples.parchisYOca.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.samples.parchisYOca.achievement.exceptions.AchievementAlreadyExists;
import org.springframework.samples.parchisYOca.achievement.exceptions.NameAlreadyExists;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.player.PlayerService;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    private PlayerService playerService;

    public AchievementService(PlayerService playerService){
    this.playerService = playerService;
    }

    @Transactional(readOnly = true)
    public Collection<Achievement> findAll() throws DataAccessException{
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Slice<Achievement> findAllPaging(Pageable pageable) throws DataAccessException{
        return achievementRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Achievement> findAchievementById(int achievementId) throws DataAccessException{
        return achievementRepository.findById(achievementId);
    }

    @Transactional(rollbackFor = AchievementAlreadyExists.class)
    public Achievement save(Achievement achievement) throws DataAccessException, NumberFormatException, AchievementAlreadyExists, NameAlreadyExists {
        //Line to check if the user input is a number
        Integer auxNumber = Integer.parseInt(achievement.getNumberToBeat());

        Iterable<Achievement> achievementIterable = findAll();
        for(Achievement a : achievementIterable){
            if(a.getNumberToBeat().equals(achievement.getNumberToBeat()) && a.getDescription().equals(achievement.getDescription())){
                throw new AchievementAlreadyExists();
            }
            if(a.getName().equals(achievement.getName())){
                throw new NameAlreadyExists();
            }
        }
        return achievementRepository.save(achievement);
    }

    @Transactional
    public void delete(Achievement achievement) throws DataAccessException{
        Collection<Player> players = playerService.findAll();
        for(Player p : players){
            Set<Achievement> achievements = p.getAchievements();
            if(achievements.contains(achievement)){
                achievements.remove(achievement);
                p.setAchievements(achievements);
                playerService.savePlayer(p);
            }
        }
        achievementRepository.delete(achievement);
    }

    public void checkGooseAchievements(PlayerGooseStats playerStats){
        Player player = playerStats.getPlayer();
        Collection<Achievement> oldAchievements = player.getAchievements();
        Iterable<Achievement> achievements = findAll();
        for(Achievement a : achievements){
            Integer numberToBeat = Integer.parseInt(a.getNumberToBeat());
            if(a.getDescription().equals("Number of times landed on goose squares")){
                if(playerStats.getLandedGeese() >= numberToBeat){
                    oldAchievements.add(a);
                }
            }else if(a.getDescription().equals("Number of times landed on dice squares")){
                if(playerStats.getLandedDice() >= numberToBeat){
                    oldAchievements.add(a);
                }
            }else if(a.getDescription().equals("Number of goose games won")){
                if(playerStats.getHasWon() >= numberToBeat){
                    oldAchievements.add(a);
                }
            }
        }
        playerService.savePlayer(player);
    }

    public void checkLudoAchievements(PlayerLudoStats playerStats){
        Player player = playerStats.getPlayer();
        Collection<Achievement> oldAchievements = player.getAchievements();
        Iterable<Achievement> achievements = findAll();
        for(Achievement a : achievements){
            Integer numberToBeat = Integer.parseInt(a.getNumberToBeat());
            if(a.getDescription().equals("Number of eaten tokens")){
                if(playerStats.getEatenTokens() >= numberToBeat){
                    oldAchievements.add(a);
                }
            }else if(a.getDescription().equals("Number of walked squares")){
                if(playerStats.getWalkedSquares() >= numberToBeat){
                    oldAchievements.add(a);
                }
            }else if(a.getDescription().equals("Number of ludo games won")){
                if(playerStats.getHasWon() >= numberToBeat){
                    oldAchievements.add(a);
                }
            }
        }
        playerService.savePlayer(player);
    }
}
