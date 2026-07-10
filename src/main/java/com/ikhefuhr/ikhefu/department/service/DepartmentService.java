package com.ikhefuhr.ikhefu.department.service;

import com.ikhefuhr.ikhefu.department.dto.request.CreateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.request.UpdateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    List<DepartmentResponse> getAllDepartments();

    DepartmentResponse getDepartmentById(Long id);

    DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request);

    void deleteDepartment(Long id);
}