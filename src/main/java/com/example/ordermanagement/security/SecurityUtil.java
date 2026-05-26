package com.example.ordermanagement.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private CustomUserDetails getCurrentUserDetails() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("User not authenticated");
        }

        return userDetails;
    }

    public Long getCurrentUserIdAsLong() {
        return getCurrentUserDetails().getId();
    }

    public String getCurrentUserId() {
        return String.valueOf(getCurrentUserDetails().getId());
    }

    public String getCurrentUserEmail() {
        return getCurrentUserDetails().getEmail();
    }
}