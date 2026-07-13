package com.ikhefuhr.ikhefu.employee.mapper;

import com.ikhefuhr.ikhefu.employee.dto.request.CreateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.request.UpdateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.response.EmployeeResponse;
import com.ikhefuhr.ikhefu.entity.Department;
import com.ikhefuhr.ikhefu.entity.User;

public interface EmployeeMapper {

    User toEntity(CreateEmployeeRequest request, Department department);

    EmployeeResponse toResponse(User employee);

    void updateEntity(UpdateEmployeeRequest request, User employee, Department department);
}