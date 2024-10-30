package com.example.usersapp.controllers.handlers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
        @Override
        protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            String redirectUrl = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")) ? "/admin" : "/client";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }

