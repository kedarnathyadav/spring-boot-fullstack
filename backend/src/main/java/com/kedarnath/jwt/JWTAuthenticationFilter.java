package com.kedarnath.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//        }
//
//        String jwt = authHeader.substring(7);
//        String subject = jwtUtil.getSubject(jwt);
//
//
//        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
//            if (jwtUtil.isTokenVald(jwt
//                    , userDetails.getUsername())) {
//                UsernamePasswordAuthenticationToken authenticationToken =
//                        new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                userDetails.getAuthorities()
//                        );
//                authenticationToken.setDetails(
//                        new WebAuthenticationDetailsSource().buildDetails(request)
//                );
//                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//            }
//        }
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Check if the Authorization header is null or does not start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continue the filter chain
            return; // Exit early
        }

        String jwt = authHeader.substring(7).trim(); // Extract the JWT token (after "Bearer ")

        if (jwt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is missing");
            return; // Exit early
        }

        // Get the subject from the JWT token
        String subject = jwtUtil.getSubject(jwt);

        // Proceed only if the subject is not null and the user is not already authenticated
        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            // Validate the JWT token
            if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                // Create the authentication token
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token is invalid");
                return; // Exit early
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

}
