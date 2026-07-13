package com.ikhefuhr.ikhefu.employee.dto.response;

import com.ikhefuhr.ikhefu.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Long departmentId;
    private String departmentName; // Nice inclusion for clean frontend rendering
    private boolean enabled;
    private boolean firstLogin;
    private LocalDateTime createdAt;
}