package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.TrainingSession;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.dto.TrainingSessionDTO;
import com.dhd.gymmanagement.repository.TrainingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingSessionService {
    
    @Autowired
    private TrainingSessionRepository trainingSessionRepository;
    
    public List<TrainingSessionDTO> getSessionsByUserAndTrainer(Integer userId, Integer trainerId) {
        List<TrainingSession> sessions = trainingSessionRepository.findByUserIdAndTrainerId(userId, trainerId);
        return sessions.stream()
                .map(TrainingSessionDTO::new)
                .collect(Collectors.toList());
    }
    
    public List<TrainingSession> getSessionsByTrainer(Integer trainerId) {
        return trainingSessionRepository.findByTrainerId(trainerId);
    }
    
    public List<TrainingSession> getSessionsByTrainerAndDate(Integer trainerId, Date sessionDate) {
        return trainingSessionRepository.findByTrainerIdAndDate(trainerId, sessionDate);
    }
    
    public List<TrainingSession> getSessionsByUser(Integer userId) {
        return trainingSessionRepository.findByUserId(userId);
    }
    
    public List<TrainingSession> getSessionsByStatus(TrainingSession.Status status) {
        return trainingSessionRepository.findByStatus(status);
    }
    
    public List<TrainingSession> getUpcomingSessionsByTrainer(Integer trainerId, Date startDate) {
        return trainingSessionRepository.findUpcomingSessionsByTrainer(trainerId, startDate);
    }
    
    public List<TrainingSession> getTodaySessions(Integer trainerId) {
        Date today = Date.valueOf(LocalDate.now());
        return trainingSessionRepository.findByTrainerIdAndDate(trainerId, today);
    }
    
    public TrainingSession updateSessionStatus(Integer sessionId, TrainingSession.Status status) {
        TrainingSession session = trainingSessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setStatus(status);
            return trainingSessionRepository.save(session);
        }
        return null;
    }
    
    public TrainingSession updateSessionNotes(Integer sessionId, String notes) {
        TrainingSession session = trainingSessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setNotes(notes);
            return trainingSessionRepository.save(session);
        }
        return null;
    }
    
    public void deleteSession(Integer sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            session.setIsDeleted(1);
            trainingSessionRepository.save(session);
        }
    }
    
    public TrainingSession createSession(User user, Trainer trainer, Date sessionDate, LocalTime startTime, LocalTime endTime, String notes) {
        TrainingSession session = new TrainingSession();
        session.setUser(user);
        session.setTrainer(trainer);
        session.setSessionDate(sessionDate);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setStatus(TrainingSession.Status.SCHEDULED);
        session.setNotes(notes);
        
        return trainingSessionRepository.save(session);
    }
}
