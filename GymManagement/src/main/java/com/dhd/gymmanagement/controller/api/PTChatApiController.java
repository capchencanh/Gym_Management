package com.dhd.gymmanagement.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import com.dhd.gymmanagement.entity.Trainer;
import com.dhd.gymmanagement.repository.TrainerRepository;
import com.dhd.gymmanagement.service.FirebaseChatService;

@RestController
@RequestMapping("/api/pt")
public class PTChatApiController {
    
    @Autowired
    private TrainerRepository trainerRepository;
    
    @Autowired
    private FirebaseChatService firebaseChatService;
    


    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPT() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();
            
            Optional<Trainer> trainerOpt = trainerRepository.findByUserEmail(currentUsername);
            
            if (trainerOpt.isPresent()) {
                Trainer trainer = trainerOpt.get();
                Long ptId = trainer.getUser().getUserId().longValue();
                String trainerName = trainer.getUser().getName();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("ptId", ptId);
                response.put("trainerName", trainerName);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("error", "Không tìm thấy thông tin PT");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/conversations/{ptId}")
    public ResponseEntity<?> getPTConversations(@PathVariable Long ptId) {
        try {

            List<Map<String, Object>> conversations = firebaseChatService.getConversationsForPT(ptId).get();
            
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/conversations/{ptId}/messages/{userId}")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable Long ptId,
            @PathVariable Long userId) {
        try {

            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
