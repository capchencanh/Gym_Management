package com.dhd.gymmanagement.service;

import com.dhd.gymmanagement.entity.PasswordResetToken;
import com.dhd.gymmanagement.entity.User;
import com.dhd.gymmanagement.repository.PasswordResetTokenRepository;
import com.dhd.gymmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public boolean sendPasswordResetEmail(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return false;
            }
            
            User user = userOpt.get();

            deleteExistingTokens(user.getUserId());
            String token = createNewToken(user);
            return sendEmail(email, user.getName(), token);
        } catch (Exception e) {
            System.err.println("Error in sendPasswordResetEmail: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isValidToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();
        return !resetToken.isUsed() && !resetToken.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = resetTokenOpt.get();
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = resetToken.getUser();
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }

    @Transactional
    protected void deleteExistingTokens(Integer userId) {
        tokenRepository.deleteAllByUserId(userId);
        tokenRepository.flush();
    }

    @Transactional
    protected String createNewToken(User user) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        resetToken.setUsed(false);

        PasswordResetToken savedToken = tokenRepository.save(resetToken);
        tokenRepository.flush();
        return savedToken.getToken();
    }

    private boolean sendEmail(String email, String userName, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("danhvip83@gmail.com");
            message.setTo(email);
            message.setSubject("Đặt lại mật khẩu - Gym Management");
            message.setText(String.format("""
                Xin chào %s,
                
                Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Gym Management.
                
                Vui lòng click vào link sau để đặt lại mật khẩu:
                
                http://localhost:8080/reset-password?token=%s
                
                Link này sẽ hết hạn sau 24 giờ.
                
                Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
                
                Trân trọng,
                Gym Management Team
                Hỗ trợ: danhvip83@gmail.com""", userName, token));
            
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error in sendEmail: " + e.getMessage());
            return false;
        }
    }
}
