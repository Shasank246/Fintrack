package com.fintrack.FinTrackV2.service;

import com.fintrack.FinTrackV2.model.User;
import com.fintrack.FinTrackV2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final OtpService otpService;

    public UserService(UserRepository repository, PasswordEncoder encoder, OtpService otpService) {
        this.repository = repository;
        this.encoder = encoder;
        this.otpService = otpService;
    }

    public User registerUser(User user) {
        // Check if email already exists
        Optional<User> existingUserOpt = repository.findByEmail(user.getEmail());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.isEnabled()) {
                throw new RuntimeException("Email already registered and verified. Please log in.");
            } else {
                // User exists but isn't verified. Update their password and resend OTP.
                existingUser.setPassword(encoder.encode(user.getPassword()));
                existingUser.setUsername(user.getUsername());
                User saved = repository.save(existingUser);
                otpService.sendOtp(saved);
                return saved;
            }
        }
        
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(false); // disabled until OTP verified
        User saved = repository.save(user);
        // Send OTP email
        otpService.sendOtp(saved);
        return saved;
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<User> optUser = repository.findByEmail(email);
        if (optUser.isEmpty()) return false;
        User user = optUser.get();
        if (otpService.verifyOtp(user, otp)) {
            user.setEnabled(true);
            user.setOtpVerified(true);
            user.setOtpCode(null);
            repository.save(user);
            return true;
        }
        return false;
    }

    public void resendOtp(String email) {
        repository.findByEmail(email).ifPresent(otpService::sendOtp);
    }

    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    /**
     * Save profile picture - stores as base64 data URL for simplicity.
     * In production, save to disk/S3 and store the URL.
     */
    public void updateProfilePic(String email, MultipartFile file) throws IOException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String contentType = file.getContentType();
            String dataUrl = "data:" + contentType + ";base64," + base64;
            user.setProfilePic(dataUrl);
            repository.save(user);
        }
    }
}
