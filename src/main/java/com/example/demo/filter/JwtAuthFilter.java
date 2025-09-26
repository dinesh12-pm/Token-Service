package com.example.demo.filter;

import com.example.demo.service.AccessTokenService;
import com.example.demo.service.CustomUserDetailsService;
import com.example.demo.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AccessTokenService accessTokenService;

    public JwtAuthFilter(JWTUtil jwtUtil, CustomUserDetailsService customUserDetailsService, AccessTokenService accessTokenService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.accessTokenService = accessTokenService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
         String jwt =null;
         String userName= null;
       String authHeader =  request.getHeader("Authorization");
      // String token = request.getParameter("access_token");
           if(authHeader!=null && authHeader.startsWith("Bearer ")){
               jwt = authHeader.substring(7);
               userName = jwtUtil.extractUserName(jwt); //parse token and get userName
           }

       if(userName!= null && SecurityContextHolder.getContext().getAuthentication()==null){
        UserDetails userDetails =customUserDetailsService.loadUserByUsername(userName);
       if (jwtUtil.validateToken(userName,userDetails,jwt)){
           // Our context will have more information about this token and principal object
           UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
           authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
           SecurityContextHolder.getContext().setAuthentication(authToken);
       }

       }
       filterChain.doFilter(request,response);

        // TODO validate the token
        // TODO set to security context
    }
}

//package com.example.demo.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class AccessTokenFilter extends OncePerRequestFilter {
//
//    private final TokenService tokenService;
//    private final CustomUserDetailsService userDetailsService;
//
//    public AccessTokenFilter(TokenService tokenService, CustomUserDetailsService userDetailsService) {
//        this.tokenService = tokenService;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // 1. Extract token (from param or header)
//        String token = request.getParameter("access_token");
//        if (token == null) {
//            String authHeader = request.getHeader("Authorization");
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                token = authHeader.substring(7);
//            }
//        }
//
//        // 2. Validate token
//        if (token != null && tokenService.validateToken(token)) {
//            // 3. Extract username from token
//            String username = tokenService.extractUsername(token);
//
//            // 4. Load user details from DB
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//            // 5. Create authentication object
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities());
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            // 6. Set SecurityContext
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        // Continue filter chain
//        filterChain.doFilter(request, response);
//    }
//}
//

//
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class TokenService {
//
//    // In production -> use DB/Redis instead
//    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
//
//    public String generateToken(String username) {
//        String token = java.util.UUID.randomUUID().toString();
//        tokenStore.put(token, username);
//        return token;
//    }
//
//    public boolean validateToken(String token) {
//        return tokenStore.containsKey(token);
//    }
//
//    public String extractUsername(String token) {
//        return tokenStore.get(token);
//    }
//
//    public void revokeToken(String token) {
//        tokenStore.remove(token);
//    }
//}


