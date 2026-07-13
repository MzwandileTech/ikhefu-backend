package com.ikhefuhr.ikhefu.employee.controller;

import com.ikhefuhr.ikhefu.employee.dto.request.CreateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.request.UpdateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.response.EmployeeResponse;
import com.ikhefuhr.ikhefu.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Create a new employee.
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody CreateEmployeeRequest request) {

        EmployeeResponse response = employeeService.createEmployee(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve all employees.
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {

        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     * Retrieve an employee by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    /**
     * Update employee information.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(id, request)
        );
    }

    /**
     * Disable an employee account.
     */
    @PatchMapping("/{id}/disable")
    public ResponseEntity<String> disableEmployee(
            @PathVariable Long id) {

        employeeService.disableEmployee(id);

        return ResponseEntity.ok("Employee account disabled successfully.");
    }

    /**
     * Enable an employee account.
     */
    @PatchMapping("/{id}/enable")
    public ResponseEntity<String> enableEmployee(
            @PathVariable Long id) {

        employeeService.enableEmployee(id);

        return ResponseEntity.ok("Employee account enabled successfully.");
    }

    /**
     * Delete an employee record permanently.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee record deleted permanently from the system.");
    }
}