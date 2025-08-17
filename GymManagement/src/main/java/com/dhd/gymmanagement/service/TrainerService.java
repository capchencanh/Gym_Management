package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.repository.TrainerRepository;
import com.dhd.gymmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {
    
    @Autowired
    private TrainerRepository trainerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAllActiveWithUser();
    }
    
    public Optional<Trainer> getTrainerById(Integer trainerId) {
        return trainerRepository.findById(trainerId);
    }
    
    public List<Trainer> searchTrainers(String keyword) {
        return trainerRepository.findByUser_NameContainingIgnoreCaseOrUser_EmailContainingIgnoreCaseOrUser_PhoneNumberContainingIgnoreCase(
            keyword, keyword, keyword);
    }
    
    public Trainer createTrainer(Trainer trainer) {
        if (trainer.getUser() != null) {
            trainer.getUser().setRole(User.Role.PT);
            trainer.getUser().setCreatedAt(new Timestamp(System.currentTimeMillis()));
            trainer.getUser().setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }
        return trainerRepository.save(trainer);
    }
    
    public Trainer updateTrainer(Integer trainerId, Trainer trainerDetails) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trainer"));
        
        if (trainerDetails.getUser() != null) {
            User user = trainer.getUser();
            User userDetails = trainerDetails.getUser();
            
            user.setName(userDetails.getName());
            user.setEmail(userDetails.getEmail());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setGender(userDetails.getGender());
            user.setBirthdate(userDetails.getBirthdate());
            user.setHeight(userDetails.getHeight());
            user.setWeight(userDetails.getWeight());
            user.setFitnessGoal(userDetails.getFitnessGoal());
            user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            userRepository.save(user);
        }
        
        trainer.setSpecialization(trainerDetails.getSpecialization());
        trainer.setSchedule(trainerDetails.getSchedule());
        
        return trainerRepository.save(trainer);
    }
    
    public List<Trainer> getActiveTrainers() {
        return trainerRepository.findByUser_IsDeleted(0);
    }
    
    // Method đơn giản để save trainer
    public Trainer save(Trainer trainer) {
        return trainerRepository.save(trainer);
    }
}
