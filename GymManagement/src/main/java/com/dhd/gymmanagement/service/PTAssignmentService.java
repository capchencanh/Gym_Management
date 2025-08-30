package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.*;
import com.dhd.gymmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.dhd.gymmanagement.dto.PTAssignmentDTO;

@Service
public class PTAssignmentService {
    
    @Autowired
    private PTAssignmentRepository ptAssignmentRepository;
    
    @Autowired
    private UserAvailabilityRepository userAvailabilityRepository;
    
    @Autowired
    private TrainingSessionRepository trainingSessionRepository;
    

    public PTAssignment createAssignment(User user, Trainer trainer) {
        PTAssignment assignment = new PTAssignment();
        assignment.setUser(user);
        assignment.setTrainer(trainer);
        assignment.setStatus(PTAssignment.Status.ASSIGNED);
        assignment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        return ptAssignmentRepository.save(assignment);
    }
    

    public PTAssignment getAssignmentById(Integer assignmentId) {
        return ptAssignmentRepository.findById(assignmentId).orElse(null);
    }
    

    public List<PTAssignment> getPendingAssignments() {
        return ptAssignmentRepository.findPendingAssignments();
    }
    
    public Page<PTAssignment> getAssignmentsByStatus(PTAssignment.Status status, Pageable pageable) {
        return ptAssignmentRepository.findByStatus(status, pageable);
    }
    

    public List<PTAssignment> getAssignmentsByTrainer(Integer trainerId) {
        return ptAssignmentRepository.findByTrainerId(trainerId);
    }
    

    public List<PTAssignment> getAssignmentsByUser(Integer userId) {
        return ptAssignmentRepository.findByUserId(userId);
    }
    

    public List<PTAssignmentDTO> getAssignmentsByUserAsDTO(Integer userId) {
        List<PTAssignment> assignments = ptAssignmentRepository.findByUserId(userId);
        List<PTAssignmentDTO> dtos = new ArrayList<>();
        
        if (assignments != null && !assignments.isEmpty()) {
            for (PTAssignment assignment : assignments) {
                PTAssignmentDTO dto = new PTAssignmentDTO(assignment);
                dtos.add(dto);
            }
        }
        
        return dtos;
    }
    

    public List<PTAssignment> getAllAssignments() {
        return ptAssignmentRepository.findAll();
    }
    
    public Page<PTAssignment> getAllAssignments(Pageable pageable) {
        return ptAssignmentRepository.findAll(pageable);
    }
    

    public PTAssignment updateAssignment(PTAssignment assignment) {
        assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return ptAssignmentRepository.save(assignment);
    }
    

    public PTAssignment changeStatus(Integer assignmentId, PTAssignment.Status status) {
        PTAssignment assignment = getAssignmentById(assignmentId);
        if (assignment != null) {
            assignment.setStatus(status);
            assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return ptAssignmentRepository.save(assignment);
        }
        return null;
    }
    

    public boolean updateAssignmentStatusByUserAndTrainer(Integer userId, Integer trainerId, PTAssignment.Status newStatus) {
        List<PTAssignment> assignments = getAssignmentsByUser(userId);
        for (PTAssignment assignment : assignments) {
            if (assignment.getTrainer() != null && 
                assignment.getTrainer().getTrainerId().equals(trainerId)) {
                

                if (assignment.getStatus() == PTAssignment.Status.PENDING || 
                    assignment.getStatus() == PTAssignment.Status.ASSIGNED) {
                    assignment.setStatus(newStatus);
                    assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    ptAssignmentRepository.save(assignment);
                    return true;
                }
            }
        }
        return false;
    }
    

    public boolean updateAssignmentStatusById(Integer assignmentId, PTAssignment.Status newStatus) {
        PTAssignment assignment = getAssignmentById(assignmentId);
        
        if (assignment != null) {
            if (assignment.getStatus() == PTAssignment.Status.PENDING || 
                assignment.getStatus() == PTAssignment.Status.ASSIGNED) {
                
                assignment.setStatus(newStatus);
                assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                ptAssignmentRepository.save(assignment);
                return true;
            }
        }
        return false;
    }
    

    public PTAssignment createAssignmentFromRequest(User user, String requestNotes) {
        PTAssignment assignment = new PTAssignment();
        assignment.setUser(user);
        assignment.setTrainer(null); // Chưa có trainer
        assignment.setStatus(PTAssignment.Status.PENDING);
        assignment.setRequestNotes(requestNotes);
        assignment.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        return ptAssignmentRepository.save(assignment);
    }
    

    public void deleteAssignment(Integer assignmentId) {
        PTAssignment assignment = getAssignmentById(assignmentId);
        if (assignment != null) {
            assignment.setIsDeleted(1);
            assignment.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            ptAssignmentRepository.save(assignment);
        }
    }
    

    public long countTotalAssignments() {
        return ptAssignmentRepository.count();
    }
    

    public long countPendingAssignments() {
        return ptAssignmentRepository.findPendingAssignments().size();
    }
    

    public long countActiveAssignments() {
        return ptAssignmentRepository.findActiveAssignments().size();
    }
    

    public long countCompletedAssignments() {
        List<PTAssignment> assignments = ptAssignmentRepository.findByStatus(PTAssignment.Status.COMPLETED);
        return assignments.size();
    }
}
