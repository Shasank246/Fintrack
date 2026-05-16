package com.fintrack.FinTrackV2.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TwoFactorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Only apply interceptor to authenticated users
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            HttpSession session = request.getSession(false);
            Boolean is2faVerified = session != null ? (Boolean) session.getAttribute("2fa_verified") : null;

            // If 2fa is not verified, and they are trying to access protected resources, block them
            if (is2faVerified == null || !is2faVerified) {
                String path = request.getRequestURI();
                // Allow them to access the 2FA verification pages themselves
                if (!path.equals("/login-verify-otp") && !path.equals("/login-resend-otp") && !path.equals("/logout")) {
                    response.sendRedirect("/login-verify-otp");
                    return false;
                }
            }
        }
        return true;
    }
}
