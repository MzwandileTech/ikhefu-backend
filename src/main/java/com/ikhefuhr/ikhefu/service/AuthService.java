package com.ikhefuhr.ikhefu.service;

import com.ikhefuhr.ikhefu.dto.request.LoginRequest;
import com.ikhefuhr.ikhefu.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

}
