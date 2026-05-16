package com.fintrack.FinTrackV2.config;

import com.fintrack.FinTrackV2.model.User;
import com.fintrack.FinTrackV2.repository.UserRepository;
import com.fintrack.FinTrackV2.service.OtpService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // Generate and send OTP
            otpService.sendOtp(user);
            // Set session attribute indicating 2FA is required and not yet verified
            request.getSession().setAttribute("2fa_verified", false);
        }

        // Redirect to OTP verification page for login
        response.sendRedirect("/login-verify-otp");
    }
}
