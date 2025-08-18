package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.ClassDTO;
import com.dhd.gymmanagement.entity.TrainingClass;
import com.dhd.gymmanagement.service.TrainingClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassApiController {

    @Autowired
    private TrainingClassService trainingClassService;

    @GetMapping("/classes")
    public ResponseEntity<List<ClassDTO>> getAllClasses() {
        try {
            List<TrainingClass> classes = trainingClassService.getAllClasses();
            List<ClassDTO> classDTOs = classes.stream()
                .map(ClassDTO::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(classDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/classes/{id}")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable Integer id) {
        try {
            TrainingClass trainingClass = trainingClassService.getClassById(id);
            if (trainingClass != null) {
                return ResponseEntity.ok(new ClassDTO(trainingClass));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
