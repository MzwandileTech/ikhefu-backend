package com.ikhefuhr.ikhefu.auth.service;

import com.ikhefuhr.ikhefu.auth.dto.request.LoginRequest;
import com.ikhefuhr.ikhefu.auth.dto.response.LoginResponse;
import org.springframework.stereotype.Service;
import com.ikhefuhr.ikhefu.entity.User;
import com.ikhefuhr.ikhefu.repository.UserRepository;
import com.ikhefuhr.ikhefu.security.jwt.JwtService;
import com.ikhefuhr.ikhefu.security.service.CustomUserDetailsService;
import com.ikhefuhr.ikhefu.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public LoginResponse login(LoginRequest request) {

        // Authenticate the user's credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Retrieve the user from the database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Load the user as UserDetails for JWT generation
        UserDetails userDetails = customUserDetailsService
                .loadUserByUsername(user.getEmail());

        // Generate JWT token
        String token = jwtService.generateToken(userDetails);

        // Return the login response
        return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .build();
    }
}