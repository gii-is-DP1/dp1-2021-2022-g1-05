package org.springframework.samples.parchisYOca.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.parchisYOca.achievement.exceptions.DuplicatedAchievementNameException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Transactional(readOnly = true)
    public int achievementCount() throws DataAccessException {
        return (int) achievementRepository.count();
    }

    @Transactional(readOnly = true)
    public Iterable<Achievement> findAll() throws DataAccessException{
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Achievement> findAchievementById(int achievementId) throws DataAccessException{
        return achievementRepository.findById(achievementId);
    }

    /* //MÉTODO SAVE SIN FLORITURAS, VOLVER A USAR ESTE SI NOS TIRAN PARA ATRÁS EL OTRO
    @Transactional
    public void save(Achievement achievement) throws DataAccessException{
        achievementRepository.save(achievement);
    }
    */

    @Transactional(rollbackFor = DuplicatedAchievementNameException.class)
    public void save(Achievement achievement) throws DataAccessException, DuplicatedAchievementNameException{
        Iterable<Achievement> achievements = achievementRepository.findAll();
        boolean repeFlag = false;
        for (Achievement achievementToTest: achievements) {
            if(achievement.getName().equals(achievementToTest.getName())) {
                repeFlag = true;
                break;
            }
        }
        if(repeFlag){
            throw new DuplicatedAchievementNameException();
        } else {
            achievementRepository.save(achievement);
        }
    }

    public void delete(Achievement achievement) throws DataAccessException{
        achievementRepository.delete(achievement);
    }
}
