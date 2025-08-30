package com.dhd.gymmanagement.service;

import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseChatService {

    private final FirebaseDatabase firebaseDatabase;

    public FirebaseChatService(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }


    public String createChatRoomId(Long userId1, Long userId2) {

        if (userId1 < userId2) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    /**
     * Gửi tin nhắn
     */
    public CompletableFuture<String> sendMessage(Long senderId, Long receiverId, String message) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            String chatRoomId = createChatRoomId(senderId, receiverId);
            
            DatabaseReference chatRef = firebaseDatabase.getReference("chats/" + chatRoomId);
            
            Map<String, Object> newMessage = Map.of(
                "senderId", senderId,
                "receiverId", receiverId,
                "message", message,
                "timestamp", ServerValue.TIMESTAMP,
                "read", false
            );
            

            createOrUpdateChatRoom(senderId, receiverId, message).thenRun(() -> {

                chatRef.push().setValue(newMessage, (error, ref) -> {
                    if (error != null) {
                        future.completeExceptionally(error.toException());
                    } else {
                        future.complete(ref.getKey());
                    }
                });
            }).exceptionally(throwable -> {
                future.completeExceptionally(throwable);
                return null;
            });
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }


    public CompletableFuture<List<ChatMessage>> getConversationMessages(Long userId1, Long userId2) {
        CompletableFuture<List<ChatMessage>> future = new CompletableFuture<>();
        
        try {
            String chatRoomId = createChatRoomId(userId1, userId2);
            DatabaseReference chatRef = firebaseDatabase.getReference("chats/" + chatRoomId);
            
            chatRef.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<ChatMessage> messages = new java.util.ArrayList<>();
                    
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                        if (message != null) {
                            message.setMessageId(messageSnapshot.getKey());
                            messages.add(message);
                        }
                    }
                    
                    future.complete(messages);
                }
                
                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }

    /**
     * Lấy danh sách conversations của một PT
     */
    public CompletableFuture<List<Map<String, Object>>> getConversationsForPT(Long ptId) {
        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
        List<Map<String, Object>> conversations = new java.util.ArrayList<>();
        

        final int[] pendingOperations = {0};
        final int[] completedOperations = {0};
        

        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                if (!future.isDone()) {
                    future.complete(conversations);
                }
            }
        }, 10000);
        
        try {
            DatabaseReference chatRoomsRef = firebaseDatabase.getReference("chatRooms");

            
            chatRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot chatRoomSnapshot : snapshot.getChildren()) {
                            DataSnapshot usersSnapshot = chatRoomSnapshot.child("users");
                            

                            if (usersSnapshot.hasChild(ptId.toString())) {

                                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                    String userIdStr = userSnapshot.getKey();
                                    
                                    if (!userIdStr.equals(ptId.toString())) {
                                        Long userId = Long.parseLong(userIdStr);
                                        

                                        String chatRoomId = chatRoomSnapshot.getKey();
                                        DatabaseReference chatsRef = firebaseDatabase.getReference("chats/" + chatRoomId);
                                        

                                        pendingOperations[0]++;
                                        
                                        chatsRef.orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot chatSnapshot) {
                                                String lastMessage = "Chưa có tin nhắn";
                                                if (chatSnapshot.exists()) {
                                                    for (DataSnapshot messageSnapshot : chatSnapshot.getChildren()) {
                                                        lastMessage = messageSnapshot.child("message").getValue(String.class);
                                                        break;
                                                    }
                                                }
                                                
                                                Map<String, Object> conversation = Map.of(
                                                    "userId", userId,
                                                    "userName", "User " + userId, // Tạm thời
                                                    "lastMessage", lastMessage
                                                );
                                                
                                                conversations.add(conversation);
                                                

                                                completedOperations[0]++;
                                                

                                                if (completedOperations[0] >= pendingOperations[0]) {
                                                    timer.cancel();
                                                    future.complete(conversations);
                                                }
                                            }
                                            
                                            @Override
                                            public void onCancelled(DatabaseError error) {
                            
                                            }
                                        });
                                        
                                        break;
                                    }
                                }
                            } else {
            
                            }
                        }
                        

                        if (conversations.isEmpty()) {

                        }
                    } else {
                        timer.cancel(); // Hủy timeout
                        future.complete(conversations);
                    }
                }
                
                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(error.toException());
                }
            });
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }

    public CompletableFuture<Void> createOrUpdateChatRoom(Long userId1, Long userId2, String lastMessage) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        try {
            String chatRoomId = createChatRoomId(userId1, userId2);
            
            DatabaseReference chatRoomRef = firebaseDatabase.getReference("chatRooms/" + chatRoomId);
            
            Map<String, Object> updates = Map.of(
                "users/" + userId1, true,
                "users/" + userId2, true,
                "lastMessage", lastMessage,
                "lastActivity", ServerValue.TIMESTAMP
            );
            
            chatRoomRef.updateChildren(updates, (error, ref) -> {
                if (error != null) {
                    future.completeExceptionally(error.toException());
                } else {
                    future.complete(null);
                }
            });
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }


    public static class ChatMessage {
        private String messageId;
        private Long senderId;
        private Long receiverId;
        private String message;
        private Object timestamp;
        private boolean read;

        // Getters and Setters
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getTimestamp() { return timestamp; }
        public void setTimestamp(Object timestamp) { this.timestamp = timestamp; }
        
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
}
