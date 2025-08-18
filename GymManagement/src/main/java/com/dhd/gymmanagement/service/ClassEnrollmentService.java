package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.ClassEnrollment;
import com.dhd.gymmanagement.entity.TrainingClass;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.repository.ClassEnrollmentRepository;
import com.dhd.gymmanagement.repository.TrainingClassRepository;
import com.dhd.gymmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ClassEnrollmentService {
    
    @Autowired
    private ClassEnrollmentRepository classEnrollmentRepository;
    
    @Autowired
    private TrainingClassRepository trainingClassRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<ClassEnrollment> getEnrollmentsByClass(Integer classId) {
        return classEnrollmentRepository.findByClassId(classId);
    }

    public List<ClassEnrollment> getEnrollmentsByUser(Integer userId) {
        return classEnrollmentRepository.findByUserId(userId);
    }

    public ClassEnrollment getEnrollmentById(Integer enrollmentId) {
        Optional<ClassEnrollment> enrollment = classEnrollmentRepository.findById(enrollmentId);
        if (enrollment.isPresent() && enrollment.get().getIsDeleted() == 0) {
            return enrollment.get();
        }
        return null;
    }

    public ClassEnrollment enrollUser(Integer userId, Integer classId) {
        // Kiểm tra xem user đã đăng ký lớp này chưa
        Optional<ClassEnrollment> existingEnrollment = classEnrollmentRepository.findByClassIdAndUserId(classId, userId);
        if (existingEnrollment.isPresent() && existingEnrollment.get().getIsDeleted() == 0) {
            return null; // Đã đăng ký rồi
        }

        // Kiểm tra xem lớp có còn chỗ
        TrainingClass trainingClass = trainingClassRepository.findById(classId).orElse(null);
        if (trainingClass == null || trainingClass.getIsDeleted() != 0) {
            return null;
        }

        Long currentEnrolled = classEnrollmentRepository.countEnrolledUsersByClassId(classId);
        if (currentEnrolled >= trainingClass.getMaxParticipants()) {
            return null; // Lớp đầy
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        ClassEnrollment enrollment = new ClassEnrollment();
        enrollment.setUser(user);
        enrollment.setTrainingClass(trainingClass);
        enrollment.setJoinedAt(new Timestamp(System.currentTimeMillis()));
        enrollment.setStatus(ClassEnrollment.Status.ENROLLED);
        enrollment.setIsDeleted(0);

        return classEnrollmentRepository.save(enrollment);
    }

    public boolean cancelEnrollment(Integer enrollmentId) {
        Optional<ClassEnrollment> enrollment = classEnrollmentRepository.findById(enrollmentId);
        if (enrollment.isPresent() && enrollment.get().getIsDeleted() == 0) {
            ClassEnrollment enrollmentToCancel = enrollment.get();
            enrollmentToCancel.setStatus(ClassEnrollment.Status.CANCELLED);
            enrollmentToCancel.setIsDeleted(1);
            enrollmentToCancel.setJoinedAt(new Timestamp(System.currentTimeMillis()));
            classEnrollmentRepository.save(enrollmentToCancel);
            return true;
        }
        return false;
    }

    public boolean markAttendance(Integer enrollmentId, Boolean attendance) {
        Optional<ClassEnrollment> enrollment = classEnrollmentRepository.findById(enrollmentId);
        if (enrollment.isPresent() && enrollment.get().getIsDeleted() == 0) {
            ClassEnrollment enrollmentToUpdate = enrollment.get();
            enrollmentToUpdate.setAttendance(attendance);
            if (attendance) {
                enrollmentToUpdate.setCheckInTime(new Timestamp(System.currentTimeMillis()));
            }
            classEnrollmentRepository.save(enrollmentToUpdate);
            return true;
        }
        return false;
    }

    public boolean completeEnrollment(Integer enrollmentId) {
        Optional<ClassEnrollment> enrollment = classEnrollmentRepository.findById(enrollmentId);
        if (enrollment.isPresent() && enrollment.get().getIsDeleted() == 0) {
            ClassEnrollment enrollmentToComplete = enrollment.get();
            enrollmentToComplete.setStatus(ClassEnrollment.Status.COMPLETED);
            classEnrollmentRepository.save(enrollmentToComplete);
            return true;
        }
        return false;
    }

    public List<ClassEnrollment> getEnrolledUsersByClass(Integer classId) {
        return classEnrollmentRepository.findEnrolledUsersByClassId(classId);
    }
    
    public List<ClassEnrollment> getAllEnrollmentsByClass(Integer classId) {
        return classEnrollmentRepository.findAllEnrollmentsByClassId(classId);
    }

    public Long getEnrolledCountByClass(Integer classId) {
        return classEnrollmentRepository.countEnrolledUsersByClassId(classId);
    }

    public boolean isUserEnrolled(Integer userId, Integer classId) {
        Optional<ClassEnrollment> enrollment = classEnrollmentRepository.findByClassIdAndUserId(classId, userId);
        return enrollment.isPresent() && enrollment.get().getIsDeleted() == 0;
    }
}
