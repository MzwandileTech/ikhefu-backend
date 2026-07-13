package com.ikhefuhr.ikhefu.employee.service;

import com.ikhefuhr.ikhefu.employee.dto.request.CreateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.request.UpdateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    List<EmployeeResponse> getAllEmployees();

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);

    void disableEmployee(Long id);

    void enableEmployee(Long id);

    void deleteEmployee(Long id);
}