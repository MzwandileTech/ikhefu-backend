package com.ikhefuhr.ikhefu.department.mapper;

import com.ikhefuhr.ikhefu.department.dto.request.CreateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.request.UpdateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.response.DepartmentResponse;
import com.ikhefuhr.ikhefu.entity.Department;

public interface DepartmentMapper {

    Department toEntity(CreateDepartmentRequest request);

    DepartmentResponse toResponse(Department department);

    void updateEntity(UpdateDepartmentRequest request, Department department);
}