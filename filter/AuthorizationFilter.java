package linkedhu.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import linkedhu.jwt.JWTHandler;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getServletPath().equals("/api/v1/login"))
            filterChain.doFilter(request, response);

        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring("Bearer ".length());
                UsernamePasswordAuthenticationToken authenticationToken = JWTHandler.getAuthToken(token);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                Map<String, String> errorResponse = new HashMap<>();
                response.setStatus(403);
                errorResponse.put("status", "error");
                errorResponse.put("message", "You are not authorized");
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
            }
        }

        filterChain.doFilter(request, response);
    }
}
