package com.dhd.gymmanagement.controller.pt;

import com.dhd.gymmanagement.entity.WorkoutLogComment;
import com.dhd.gymmanagement.service.WorkoutLogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pt/comments")
public class PTLogController {

    @Autowired
    private WorkoutLogCommentService commentService;


    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, String> payload) {
        try {
            Integer logId = Integer.parseInt(payload.get("logId"));
            Integer ptId = Integer.parseInt(payload.get("ptId"));
            String commentText = payload.get("comment");

            if (commentText == null || commentText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Comment cannot be empty.");
            }

            WorkoutLogComment savedComment = commentService.addComment(logId, ptId, commentText);


            Map<String, Object> response = new HashMap<>();
            response.put("comment", savedComment.getComment());
            response.put("ptName", savedComment.getPt().getName());
            response.put("createdAt", savedComment.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid logId or ptId.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating comment: " + e.getMessage());
        }
    }
}
