package com.ikhefuhr.ikhefu.auth.service;

import com.ikhefuhr.ikhefu.auth.dto.request.LoginRequest;
import com.ikhefuhr.ikhefu.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request); // Or whatever your method signature is
}
