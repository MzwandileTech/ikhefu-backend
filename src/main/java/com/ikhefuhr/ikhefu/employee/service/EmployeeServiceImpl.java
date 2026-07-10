package com.ikhefuhr.ikhefu.employee.service;

import com.ikhefuhr.ikhefu.employee.dto.request.CreateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.request.UpdateEmployeeRequest;
import com.ikhefuhr.ikhefu.employee.dto.response.EmployeeResponse;
import com.ikhefuhr.ikhefu.employee.mapper.EmployeeMapper;
import com.ikhefuhr.ikhefu.entity.Department;
import com.ikhefuhr.ikhefu.entity.User;
import com.ikhefuhr.ikhefu.enums.Role; // Imported your enum
import com.ikhefuhr.ikhefu.repository.DepartmentRepository;
import com.ikhefuhr.ikhefu.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // Imported password encoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder; // 1. Injected PasswordEncoder

    @Override
    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new IllegalArgumentException("An employee with this email already exists.");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + request.getDepartmentId()));
        }

        User employee = employeeMapper.toEntity(request, department);

        // Apply default onboarding configurations
        employee.setEnabled(true);
        employee.setFirstLogin(true);

        // 2. Set the Enum Role name to satisfy the database NOT NULL constraint
        employee.setRole(Role.EMPLOYEE);

        // 3. Set a static encoded password for easy testing in Postman
        employee.setPassword(passwordEncoder.encode("Welcome@123"));

        User savedEmployee = userRepository.save(employee);
        return employeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return userRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + request.getDepartmentId()));
        }

        employeeMapper.updateEntity(request, employee, department);
        User updatedEmployee = userRepository.save(employee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void disableEmployee(Long id) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
        employee.setEnabled(false);
        userRepository.save(employee);
    }

    @Override
    @Transactional
    public void enableEmployee(Long id) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
        employee.setEnabled(true);
        userRepository.save(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}