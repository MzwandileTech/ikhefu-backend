package com.ikhefuhr.ikhefu.department.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartmentRequest {

    @NotBlank(message = "Department name is required")
    private String name;

    @NotBlank(message = "Department code is required")
    @Size(min = 2, max = 10, message = "Code must be between 2 and 10 characters")
    private String code;

    private String description;
}