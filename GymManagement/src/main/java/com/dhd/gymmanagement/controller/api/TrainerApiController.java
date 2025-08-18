package com.dhd.gymmanagement.controller.api;

import com.dhd.gymmanagement.dto.TrainerDTO;
import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TrainerApiController {

    @Autowired
    private TrainerService trainerService;

    @GetMapping("/trainers")
    public ResponseEntity<List<TrainerDTO>> getAllTrainers() {
        try {
            List<Trainer> trainers = trainerService.getAllTrainers();
            List<TrainerDTO> trainerDTOs = trainers.stream()
                .map(TrainerDTO::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(trainerDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/trainers/{id}")
    public ResponseEntity<TrainerDTO> getTrainerById(@PathVariable Integer id) {
        try {
            Trainer trainer = trainerService.getTrainerById(id).orElse(null);
            if (trainer != null) {
                return ResponseEntity.ok(new TrainerDTO(trainer));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
