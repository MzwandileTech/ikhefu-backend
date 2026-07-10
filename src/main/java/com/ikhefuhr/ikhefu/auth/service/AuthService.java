package com.ikhefuhr.ikhefu.service;

import com.ikhefuhr.ikhefu.auth.controller.dto.request.LoginRequest;
import com.ikhefuhr.ikhefu.auth.controller.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}
