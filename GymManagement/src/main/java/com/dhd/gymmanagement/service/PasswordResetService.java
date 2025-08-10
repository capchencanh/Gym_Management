package com.dhd.gymmanagement.service;

public interface PasswordResetService {
    boolean sendPasswordResetEmail(String email);
    boolean resetPassword(String token, String newPassword);
    boolean isValidToken(String token);
}
