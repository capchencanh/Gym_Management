package com.dhd.gymmanagement.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseConfigApiController {

    @Value("${firebase.api.key}")
    private String apiKey;

    @Value("${firebase.auth.domain}")
    private String authDomain;

    @Value("${firebase.database.url}")
    private String databaseURL;

    @Value("${firebase.project.id}")
    private String projectId;

    @Value("${firebase.storage.bucket}")
    private String storageBucket;

    @Value("${firebase.messaging.sender.id}")
    private String messagingSenderId;

    @Value("${firebase.app.id}")
    private String appId;

    @Value("${firebase.measurement.id}")
    private String measurementId;

    @GetMapping("/config")
    public ResponseEntity<Map<String, String>> getFirebaseConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("apiKey", apiKey);
        config.put("authDomain", authDomain);
        config.put("databaseURL", databaseURL);
        config.put("projectId", projectId);
        config.put("storageBucket", storageBucket);
        config.put("messagingSenderId", messagingSenderId);
        config.put("appId", appId);
        config.put("measurementId", measurementId);
        
        return ResponseEntity.ok(config);
    }
}
