package org.springframework.samples.parchisYOca.achievement;

import org.springframework.data.repository.CrudRepository;
import org.springframework.samples.parchisYOca.achievement.Achievement;
import org.springframework.samples.petclinic.user.User;


public interface AchievementRepository extends  CrudRepository<Achievement, String>{

}
