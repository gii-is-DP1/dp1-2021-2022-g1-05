package org.springframework.samples.parchisYOca.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Transactional
    public int achievementCount() {
        return (int) achievementRepository.count();
    }

    @Transactional
    public Iterable<Achievement> findAll(){
        return achievementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Achievement> findAchievementById(int achievementId){
        return achievementRepository.findById(Integer.toString(achievementId));
    }

    @Transactional
    public void save(Achievement achievement){
        achievementRepository.save(achievement);
    }

    public void delete(Achievement achievement){
        achievementRepository.delete(achievement);
    }
}
