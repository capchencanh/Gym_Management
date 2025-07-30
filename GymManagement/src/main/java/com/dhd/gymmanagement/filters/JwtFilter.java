package com.dhd.gymmanagement.filters;

import com.dhd.gymmanagement.utils.JwtUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JwtFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        if (requestPath.startsWith("/css/") || requestPath.startsWith("/js/") || 
            requestPath.startsWith("/images/") || requestPath.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        logger.debug("JwtFilter processing request: {} {}", httpRequest.getMethod(), requestPath);

        String token = null;
        
        String header = httpRequest.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            logger.debug("Found JWT token in Authorization header");
        } else {
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        logger.debug("Found JWT token in cookie");
                        break;
                    }
                }
            }
        }

        if (token != null && !token.isEmpty()) {
            try {
                Map<String, Object> claims = JwtUtils.validateTokenAndGetClaims(token);
                if (claims != null) {
                    String email = (String) claims.get("email");
                    String role = (String) claims.get("role");
                    
                    String roleWithPrefix = "ROLE_" + role;
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleWithPrefix));
                    
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    logger.debug("Successfully authenticated user '{}' with role '{}' for request: {}", 
                               email, roleWithPrefix, requestPath);
                } else {
                    logger.warn("Token validation failed (claims is null) for request: {}", requestPath);
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                logger.warn("Invalid JWT token for request {}: {}", requestPath, e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("No JWT token found for request: {}", requestPath);
        }

        chain.doFilter(request, response);
    }
} 