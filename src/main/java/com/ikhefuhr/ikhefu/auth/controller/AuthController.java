package com.ikhefuhr.ikhefu.auth.controller;

import com.ikhefuhr.ikhefu.auth.service.AuthService;
import com.ikhefuhr.ikhefu.auth.dto.request.LoginRequest;
import com.ikhefuhr.ikhefu.auth.dto.response.LoginResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticate a user and return a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
