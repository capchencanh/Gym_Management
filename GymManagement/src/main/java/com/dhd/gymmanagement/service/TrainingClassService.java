package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.TrainingClass;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.repository.TrainingClassRepository;
import com.dhd.gymmanagement.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingClassService {
    
    @Autowired
    private TrainingClassRepository trainingClassRepository;
    
    @Autowired
    private TrainerRepository trainerRepository;

    public List<TrainingClass> getAllClasses() {
        return trainingClassRepository.findAllActiveWithTrainerAndUser();
    }

    public TrainingClass getClassById(Integer classId) {
        Optional<TrainingClass> trainingClass = trainingClassRepository.findById(classId);
        if (trainingClass.isPresent() && trainingClass.get().getIsDeleted() == 0) {
            return trainingClass.get();
        }
        return null;
    }

    public TrainingClass createClass(TrainingClass trainingClass) {
        trainingClass.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        trainingClass.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        trainingClass.setIsDeleted(0);
        return trainingClassRepository.save(trainingClass);
    }

    public TrainingClass updateClass(Integer classId, TrainingClass trainingClassData) {
        Optional<TrainingClass> existingClass = trainingClassRepository.findById(classId);
        if (existingClass.isPresent() && existingClass.get().getIsDeleted() == 0) {
            TrainingClass trainingClass = existingClass.get();
            trainingClass.setName(trainingClassData.getName());
            trainingClass.setDescription(trainingClassData.getDescription());
            trainingClass.setSchedule(trainingClassData.getSchedule());
            trainingClass.setMaxParticipants(trainingClassData.getMaxParticipants());
            trainingClass.setPrice(trainingClassData.getPrice());
            trainingClass.setDurationMinutes(trainingClassData.getDurationMinutes());
            trainingClass.setStartTime(trainingClassData.getStartTime());
            trainingClass.setDaysOfWeek(trainingClassData.getDaysOfWeek());
            trainingClass.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            trainingClass.setTrainer(trainingClassData.getTrainer());
            
            return trainingClassRepository.save(trainingClass);
        }
        return null;
    }

    public boolean deleteClass(Integer classId) {
        Optional<TrainingClass> trainingClass = trainingClassRepository.findById(classId);
        if (trainingClass.isPresent() && trainingClass.get().getIsDeleted() == 0) {
            TrainingClass classToDelete = trainingClass.get();
            classToDelete.setIsDeleted(1);
            classToDelete.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            trainingClassRepository.save(classToDelete);
            return true;
        }
        return false;
    }

    public List<TrainingClass> getClassesByTrainer(Integer trainerId) {
        return trainingClassRepository.findByTrainerId(trainerId);
    }

    public List<TrainingClass> searchClassesByName(String name) {
        return trainingClassRepository.findByNameContaining(name);
    }

    public List<TrainingClass> getClassesWithoutTrainer() {
        return trainingClassRepository.findClassesWithoutTrainer();
    }

    public boolean assignTrainerToClass(Integer classId, Integer trainerId) {
        Optional<TrainingClass> trainingClass = trainingClassRepository.findById(classId);
        Optional<Trainer> trainer = trainerRepository.findById(trainerId);
        
        if (trainingClass.isPresent() && trainer.isPresent() && trainingClass.get().getIsDeleted() == 0) {
            TrainingClass classToUpdate = trainingClass.get();
            classToUpdate.setTrainer(trainer.get());
            classToUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            trainingClassRepository.save(classToUpdate);
            return true;
        }
        return false;
    }

    public Long getEnrolledCount(Integer classId) {
        return trainingClassRepository.countEnrolledUsers(classId);
    }
}
