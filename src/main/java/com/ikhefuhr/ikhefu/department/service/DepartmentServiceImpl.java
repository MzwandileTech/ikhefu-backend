package com.ikhefuhr.ikhefu.department.service;

import com.ikhefuhr.ikhefu.department.dto.request.CreateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.request.UpdateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.response.DepartmentResponse;
import com.ikhefuhr.ikhefu.department.mapper.DepartmentMapper;
import com.ikhefuhr.ikhefu.entity.Department;
import com.ikhefuhr.ikhefu.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalArgumentException("A department with this name already exists.");
        }

        if (departmentRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
            throw new IllegalArgumentException("A department with this code already exists.");
        }

        Department department = departmentMapper.toEntity(request);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponse(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));
        return departmentMapper.toResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));

        if (!department.getName().equalsIgnoreCase(request.getName().trim()) &&
                departmentRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalArgumentException("A department with this name already exists.");
        }

        if (!department.getCode().equalsIgnoreCase(request.getCode().trim()) &&
                departmentRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
            throw new IllegalArgumentException("A department with this code already exists.");
        }

        departmentMapper.updateEntity(request, department);
        return departmentMapper.toResponse(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + id));

        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new IllegalStateException("Cannot delete department because it contains active employees.");
        }

        departmentRepository.delete(department);
    }
}