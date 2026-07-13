package com.ikhefuhr.ikhefu.auth.dto.response;

import com.ikhefuhr.ikhefu.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private Role role;
    private String fullName;
}
