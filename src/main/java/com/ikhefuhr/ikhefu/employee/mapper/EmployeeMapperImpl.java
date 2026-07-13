package com.ikhefuhr.ikhefu.employee.mapper;

import com.ikhefuhr.ikhefu.employee.dto.request.CreateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.request.UpdateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.response.EmployeeResponse;
import com.ikhefuhr.ikhefu.entity.Department;
import com.ikhefuhr.ikhefu.entity.User;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public User toEntity(CreateEmployeeRequest request, Department department) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .department(department)
                .build();
    }

    @Override
    public EmployeeResponse toResponse(User employee) {
        if (employee == null) {
            return null;
        }

        Long deptId = (employee.getDepartment() != null) ? employee.getDepartment().getId() : null;
        String deptName = (employee.getDepartment() != null) ? employee.getDepartment().getName() : null;

        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .role(employee.getRole())
                .departmentId(deptId)
                .departmentName(deptName)
                .enabled(employee.getEnabled())
                .firstLogin(employee.getFirstLogin())
                .createdAt(employee.getCreatedAt())
                .build();
    }

    @Override
    public void updateEntity(UpdateEmployeeRequest request, User employee, Department department) {
        if (request == null || employee == null) {
            return;
        }

        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setDepartment(department);
    }
}