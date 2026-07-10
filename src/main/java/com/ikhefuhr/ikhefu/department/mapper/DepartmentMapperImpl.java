package com.ikhefuhr.ikhefu.department.mapper;

import com.ikhefuhr.ikhefu.department.dto.request.CreateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.request.UpdateDepartmentRequest;
import com.ikhefuhr.ikhefu.department.dto.response.DepartmentResponse;
import com.ikhefuhr.ikhefu.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapperImpl implements DepartmentMapper {

    @Override
    public Department toEntity(CreateDepartmentRequest request) {
        if (request == null) {
            return null;
        }

        return Department.builder()
                .name(request.getName().trim())
                .code(request.getCode().trim().toUpperCase())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .build();
    }

    @Override
    public DepartmentResponse toResponse(Department department) {
        if (department == null) {
            return null;
        }

        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .enabled(department.getEnabled())
                .createdAt(department.getCreatedAt())
                .build();
    }

    @Override
    public void updateEntity(UpdateDepartmentRequest request, Department department) {
        if (request == null || department == null) {
            return;
        }

        department.setName(request.getName().trim());
        department.setCode(request.getCode().trim().toUpperCase());
        department.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
    }
}