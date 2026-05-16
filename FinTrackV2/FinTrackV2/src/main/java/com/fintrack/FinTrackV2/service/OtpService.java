package com.fintrack.FinTrackV2.service;

import com.fintrack.FinTrackV2.model.User;
import com.fintrack.FinTrackV2.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    public OtpService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    @Async
    public void sendOtp(User user) {
        String otp = generateOtp();
        user.setOtpCode(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("FinTrack - Email Verification OTP");
            message.setText(
                "Hello " + user.getUsername() + ",\n\n" +
                "Your OTP for FinTrack email verification is:\n\n" +
                "  " + otp + "\n\n" +
                "This OTP expires in 10 minutes.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "— FinTrack Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to send OTP email.");
            e.printStackTrace();
            throw new RuntimeException("Failed to send OTP email. Please check your SMTP configuration.", e);
        }
    }

    public boolean verifyOtp(User user, String enteredOtp) {
        if (user.getOtpCode() == null) return false;
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) return false;
        return user.getOtpCode().equals(enteredOtp.trim());
    }
}
