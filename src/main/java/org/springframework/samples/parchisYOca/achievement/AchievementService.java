package org.springframework.samples.parchisYOca.achievement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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

    @Transactional
    public void save(Achievement achievement) throws DataAccessException{
        achievementRepository.save(achievement);
    }

    public void delete(Achievement achievement) throws DataAccessException{
        achievementRepository.delete(achievement);
    }
}
