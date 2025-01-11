package com.example.usersapp.security;

import com.example.usersapp.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService  userDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("JWTAuthenticationFilter: Processing request to " + request.getRequestURI());

        String token = request.getHeader("Authorization");
        System.out.println("Authorization header: " + token);

        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwt = token.substring(7);
                System.out.println("Extracted JWT Token: " + jwt);

                String username = jwtTokenUtil.getUsernameFromToken(jwt);
                System.out.println("Username from JWT: " + username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    System.out.println("Loaded UserDetails: " + userDetails.getUsername());

                    if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                        System.out.println("JWT Token is valid for username: " + username);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        System.out.println("Invalid JWT Token for username: " + username);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error processing JWT Token: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Invalid JWT token: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("No Authorization header or Bearer token found.");
        }

        System.out.println("JWTAuthenticationFilter: Request processing complete for " + request.getRequestURI());
        chain.doFilter(request, response);
    }

}

