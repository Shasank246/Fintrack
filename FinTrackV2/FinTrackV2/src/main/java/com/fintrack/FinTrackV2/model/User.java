package com.fintrack.FinTrackV2.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    // Profile picture (stored as base64 or file path)
    @Column(length = 500)
    private String profilePic;

    // OTP fields
    private String otpCode;
    private LocalDateTime otpExpiry;
    private boolean otpVerified = false;

    // Account enabled after OTP verification
    private boolean enabled = false;

    // --- GETTERS ---
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getProfilePic() { return profilePic; }
    public String getOtpCode() { return otpCode; }
    public LocalDateTime getOtpExpiry() { return otpExpiry; }
    public boolean isOtpVerified() { return otpVerified; }
    public boolean isEnabled() { return enabled; }

    // --- SETTERS ---
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public void setOtpExpiry(LocalDateTime otpExpiry) { this.otpExpiry = otpExpiry; }
    public void setOtpVerified(boolean otpVerified) { this.otpVerified = otpVerified; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
