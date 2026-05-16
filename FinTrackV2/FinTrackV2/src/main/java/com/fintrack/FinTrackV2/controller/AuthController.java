package com.fintrack.FinTrackV2.controller;

import com.fintrack.FinTrackV2.model.User;
import com.fintrack.FinTrackV2.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    // ===== REGISTER =====
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            service.registerUser(user);
            return "redirect:/verify-otp?email=" + user.getEmail();
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }

    // ===== OTP VERIFICATION =====
    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email,
                            @RequestParam String otp,
                            Model model) {
        boolean success = service.verifyOtp(email, otp);
        if (success) {
            return "redirect:/login?verified=true";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP. Please try again.");
            return "verify-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String email, Model model) {
        service.resendOtp(email);
        model.addAttribute("email", email);
        model.addAttribute("success", "A new OTP has been sent to your email.");
        return "verify-otp";
    }

    // ===== LOGIN =====
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String verified,
                            @RequestParam(required = false) String error,
                            Model model) {
        if ("true".equals(verified)) {
            model.addAttribute("success", "Email verified! You can now log in.");
        }
        if (error != null) {
            model.addAttribute("error", "Invalid credentials or account not verified.");
        }
        return "login";
    }

    // ===== 2FA OTP ON LOGIN =====
    @GetMapping("/login-verify-otp")
    public String loginVerifyOtpPage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("email", authentication.getName());
        }
        return "login-verify-otp";
    }

    @PostMapping("/login-verify-otp")
    public String loginVerifyOtp(@RequestParam String otp,
                                 Authentication authentication,
                                 HttpSession session,
                                 Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String email = authentication.getName();
        boolean success = service.verifyOtp(email, otp);
        if (success) {
            session.setAttribute("2fa_verified", true);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid or expired OTP. Please try again.");
            return "login-verify-otp";
        }
    }

    @PostMapping("/login-resend-otp")
    public String loginResendOtp(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            service.resendOtp(email);
            model.addAttribute("email", email);
            model.addAttribute("success", "A new OTP has been sent to your email.");
        }
        return "login-verify-otp";
    }

    // ===== PROFILE PIC UPLOAD =====
    @PostMapping("/profile/upload-pic")
    public String uploadProfilePic(@RequestParam("file") MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        try {
            service.updateProfilePic(email, file);
        } catch (Exception e) {
            // silently fail - keep existing pic
        }
        return "redirect:/dashboard";
    }
}
